package dev.kkorolyov.simplefiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads providers/implementations of a service.
 * Similar to {@link java.util.ServiceLoader}, but provides additional functionality, such as instantiating providers with constructor arguments and loading from multiple resources.
 */
public class Providers<T> {
	private final Class<T> serviceType;
	private final Collection<T> providers;

	private Providers(Class<T> serviceType, Collection<T> providers) {
		this.serviceType = serviceType;
		this.providers = providers;
	}

	/**
	 * Loads providers from classes defined in each {@code META-INF/services/{fullyQualifiedServiceName}} configuration file on the classpath.
	 * @param serviceType service type to load providers for
	 * @param args constructor arguments for each provider instance
	 * @param <T> service type
	 * @return providers loaded from classes in each configuration file for {@code serviceType} and instantiated with {@code args}
	 * @throws UncheckedIOException if an IO issue occurs
	 * @throws IllegalStateException if multiple configuration files define the same class
	 * @see #fromClassNames(Class, Set, Object...)
	 */
	public static <T> Providers<T> fromConfig(Class<T> serviceType, Object... args) {
		try {
			return fromClassNames(serviceType,
					Collections.list(ClassLoader.getSystemResources("META-INF/services/" + serviceType.getName())).stream()
							.flatMap(url -> {
								try (BufferedReader in = Files.read(url.openStream())) {
									return in.lines()
											.collect(Collectors.toCollection(LinkedHashSet::new))  // Intermediate collection to allow closing the reader
											.stream();
								} catch (IOException e) {
									throw new UncheckedIOException(e);
								}
							}).collect(Collectors.toCollection(LinkedHashSet::new)),
					args);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Generates providers from supplied class names.
	 * @param serviceType provider service type
	 * @param classNames fully-qualified names of classes to instantiate as providers
	 * @param args constructor arguments for each provider instance
	 * @param <T> service type
	 * @return providers instantiated from each class in {@code classNames} with {@code args}
	 * @throws IllegalArgumentException if any class in {@code classNames} is not an instance of {@code serviceType}
	 * @throws NoSuchElementException if any class in {@code classNames} is not on the classpath
	 * @see #fromClasses(Class, Set, Object...)
	 */
	public static <T> Providers<T> fromClassNames(Class<T> serviceType, Set<String> classNames, Object... args) {
		return fromClasses(serviceType,
				classNames.stream()
						.map(name -> findClass(serviceType, name))
						.collect(Collectors.toSet()),
				args);
	}
	private static <T> Class<T> findClass(Class<T> serviceType, String name) {
		try {
			Class<?> c = Class.forName(name);

			if (!serviceType.isAssignableFrom(c)) throw new IllegalArgumentException(c + " is not an instance of " + serviceType);

			return (Class<T>) c;
		} catch (ClassNotFoundException e) {
			throw new NoSuchElementException("No class for name: " + name);
		}
	}

	/**
	 * Generates providers from new instances of supplied classes.
	 * Providers are instantiated using the constructor matching {@code args}.
	 * If a provider does not contain a constructor matching {@code args}, its no-arg constructor is used instead.
	 * @param serviceType provider service type
	 * @param classes classes to instantiate as providers
	 * @param args constructor arguments for each provider instance
	 * @param <T> service type
	 * @return providers instantiated from each class in {@code classes} with {@code args}
	 * @throws IllegalArgumentException if any class in {@code classes} has no constructor matching {@code args} nor a no-arg constructor
	 */
	public static <T> Providers<T> fromClasses(Class<T> serviceType, Set<Class<T>> classes, Object... args) {
		return fromInstances(serviceType,
				classes.stream()
						.map(c -> instantiate(c, args))
						.collect(Collectors.toSet()));
	}
	private static <T> T instantiate(Class<T> c, Object... args) {
		Constructor<T> constructor;
		boolean noArg = false;
		try {
			constructor = c.getConstructor(
					Arrays.stream(args)
							.map(Object::getClass)
							.toArray(Class[]::new));
		} catch (NoSuchMethodException e) {
			try {
				constructor = c.getConstructor();
				noArg = true;
			} catch (NoSuchMethodException e1) {
				throw new IllegalArgumentException(c + " contains no constructor matching args " + Arrays.toString(args) + " nor a no-arg constructor");
			}
		}
		try {
			return constructor.newInstance(noArg ? new Object[0] : args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generates providers from supplied instances.
	 * @param serviceType provider service type
	 * @param instances instances to use as providers
	 * @param <T> service type
	 * @return {@code instances} as providers
	 */
	public static <T> Providers<T> fromInstances(Class<T> serviceType, Set<T> instances) {
		return new Providers<>(serviceType, instances);
	}

	/**
	 * Locates the sole provider which matches a given predicate.
	 * @param predicate predicate to test
	 * @return provider matching {@code predicate}, if any
	 * @throws IllegalArgumentException if multiple providers match {@code predicate}
	 */
	public Optional<T> find(Predicate<T> predicate) {
		Collection<T> matches = findAll(predicate);

		if (matches.size() > 1) throw new IllegalArgumentException("Multiple " + serviceType + " providers match the given predicate");

		return matches.stream()
				.findFirst();
	}
	/**
	 * Locates all providers which match a given predicate.
	 * @param predicate predicate to test
	 * @return all providers matching {@code predicate}
	 */
	public Collection<T> findAll(Predicate<T> predicate) {
		return providers.stream()
				.filter(predicate)
				.collect(Collectors.toSet());
	}

	/**
	 * Like {@link #find(Predicate)}, but throws an exception if no matching provider.
	 * @param predicate predicate to test
	 * @return provider matching {@code predicate}
	 * @throws NoSuchElementException if no provider matches {@code predicate}
	 * @see #find(Predicate)
	 */
	public T get(Predicate<T> predicate) {
		return find(predicate)
				.orElseThrow(() -> new NoSuchElementException("No " + serviceType + " provider matches the given predicate"));
	}

	/** @return stream over all providers */
	public Stream<T> stream() {
		return providers.stream();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Providers<?> other = (Providers<?>) o;
		return Objects.equals(serviceType, other.serviceType)
				&& Objects.equals(providers, other.providers);
	}
	@Override
	public int hashCode() {
		return Objects.hash(serviceType, providers);
	}

	@Override
	public String toString() {
		return providers.toString();
	}
}
