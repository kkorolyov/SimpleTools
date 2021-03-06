package dev.kkorolyov.simplefiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static dev.kkorolyov.simplefuncs.function.Memoizer.memoize;
import static java.util.stream.Collectors.toSet;

/**
 * Loads providers/implementations of a service.
 * Similar to {@link java.util.ServiceLoader}, but provides additional functionality, such as instantiating providers with constructor arguments and loading from multiple resources.
 */
public final class Providers<T> implements Iterable<T> {
	// Memoize on (class, args) tuple to cache instances and support single instance providing multiple services
	private final static BiFunction<Class<?>, Collection<Object>, Object> instantiator = memoize((c, args) -> {
		Constructor constructor;
		boolean noArg = false;
		try {
			constructor = c.getConstructor(
					args.stream()
							.map(Object::getClass)
							.toArray(Class[]::new));
		} catch (NoSuchMethodException e) {
			try {
				constructor = c.getConstructor();
				noArg = true;
			} catch (NoSuchMethodException e1) {
				throw new IllegalArgumentException(c + " contains no constructor matching args " + args + " nor a no-arg constructor");
			}
		}
		try {
			return constructor.newInstance(noArg ? new Object[0] : args.toArray());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	});

	private final Class<T> serviceType;
	private final Collection<T> providers = new HashSet<>();

	private Providers(Class<T> serviceType, Iterable<? extends T> providers) {
		this.serviceType = serviceType;
		providers.forEach(this.providers::add);
	}

	/**
	 * Loads providers from classes defined as service providers in {@code module-info} files on the modulepath.
	 * Though module descriptors require providers to have public, no-arg constructors, this method will still attempt to instantiate with {@code args} just like {@link #fromConfig(Class, Object...)}.
	 * @param serviceType service type to load providers for
	 * @param args constructor arguments for each provider instance
	 * @param <T> service type
	 * @return providers loaded from classes defined as service providers for {@code serviceType} and instantiated with {@code args}
	 * @see #fromClasses(Class, Iterable, Object...)
	 */
	public static <T> Providers<T> fromDescriptor(Class<T> serviceType, Object... args) {
		return fromClasses(
				serviceType,
				ServiceLoader.load(serviceType).stream()
						.map(ServiceLoader.Provider::type)
						.collect(toSet()),
				args
		);
	}

	/**
	 * Loads providers from classes defined in each {@code META-INF/services/{fullyQualifiedServiceName}} configuration file on the classpath.
	 * @param serviceType service type to load providers for
	 * @param args constructor arguments for each provider instance
	 * @param <T> service type
	 * @return providers loaded from classes in each configuration file for {@code serviceType} and instantiated with {@code args}
	 * @throws UncheckedIOException if an IO issue occurs
	 * @throws IllegalStateException if multiple configuration files define the same class
	 * @see #fromClassNames(Class, Iterable, Object...)
	 */
	public static <T> Providers<T> fromConfig(Class<T> serviceType, Object... args) {
		try {
			return fromClassNames(
					serviceType,
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
					args
			);
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
	 * @see #fromClasses(Class, Iterable, Object...)
	 */
	public static <T> Providers<T> fromClassNames(Class<T> serviceType, Iterable<String> classNames, Object... args) {
		return fromClasses(
				serviceType,
				StreamSupport.stream(classNames.spliterator(), false)
						.map(name -> findClass(serviceType, name))
						.collect(toSet()),
				args
		);
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
	public static <T> Providers<T> fromClasses(Class<T> serviceType, Iterable<Class<? extends T>> classes, Object... args) {
		return fromInstances(
				serviceType,
				StreamSupport.stream(classes.spliterator(), false)
						.map(c -> (T) instantiator.apply(c, Arrays.asList(args)))
						.collect(toSet())
		);
	}

	/**
	 * Generates providers from supplied instances.
	 * @param serviceType provider service type
	 * @param instances instances to use as providers
	 * @param <T> service type
	 * @return {@code instances} as providers
	 */
	public static <T> Providers<T> fromInstances(Class<T> serviceType, Iterable<? extends T> instances) {
		return new Providers<>(serviceType, instances);
	}

	/**
	 * Locates the sole provider, if one exists.
	 * @throws IllegalStateException if multiple providers exist
	 */
	public Optional<T> find() {
		return find(t -> true);
	}
	/**
	 * Locates the sole provider which matches a given predicate.
	 * @param predicate predicate to test
	 * @return provider matching {@code predicate}, if any
	 * @throws IllegalStateException if multiple providers match {@code predicate}
	 */
	public Optional<T> find(Predicate<T> predicate) {
		Collection<T> matches = findAll(predicate);

		if (matches.size() > 1) throw new IllegalStateException("Multiple " + serviceType + " providers match the given predicate");

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
				.collect(toSet());
	}

	/**
	 * Like {@link #find()}, but throws an exception if no provider.
	 * @throws NoSuchElementException if no provider exists
	 * @throws IllegalStateException if multiple providers exist
	 * @see #find()
	 */
	public T get() {
		return find()
				.orElseThrow(() -> new NoSuchElementException("No " + serviceType + " provider found"));
	}
	/**
	 * Like {@link #find(Predicate)}, but throws an exception if no matching provider.
	 * @param predicate predicate to test
	 * @return provider matching {@code predicate}
	 * @throws NoSuchElementException if no provider matches {@code predicate}
	 * @throws IllegalStateException if multiple providers match {@code predicate}
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
	public Iterator<T> iterator() {
		return providers.iterator();
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
		return "Providers{" +
				"serviceType=" + serviceType +
				", providers=" + providers +
				'}';
	}
}
