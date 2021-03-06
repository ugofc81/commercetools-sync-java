package com.commercetools.sync.services;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public interface ProductService {

    /**
     * Given a {@code key}, this method first checks if cached map of product keys -&gt; ids is not empty.
     * If not, it returns a completed future that contains an optional that contains what this key maps to in
     * the cache. If the cache is empty, the method populates the cache with the mapping of all products' keys
     * to ids in the CTP project. After that, the method returns a
     * {@link CompletionStage}&lt;{@link Optional}&lt;{@link String}&gt;&gt; in which the result of it's completion
     * could contain an {@link Optional} with the id inside of it or an empty {@link Optional} if no {@link Product}
     * was found in the CTP project with this key.
     *
     * <p>Note: If the supplied key is null, this method will return an empty optional as a result of the
     * CompletionStage.
     *
     * @param key the key by which a {@link Product} id should be fetched from the CTP project.
     * @return {@link CompletionStage}&lt;{@link Optional}&lt;{@link String}&gt;&gt; in which the result of it's
     *         completion could contain an {@link Optional} with the id inside of it or an empty {@link Optional} if no
     *         {@link Product} was found in the CTP project with this key.
     */
    @Nonnull
    CompletionStage<Optional<String>> fetchCachedProductId(@Nullable final String key);

    /**
     * If not already done once before, it fetches all the product keys from the CTP project defined in a potentially
     * injected {@link SphereClient} and stores a mapping for every product to id in {@link Map}
     * and returns this cached map.
     *
     * @return {@link CompletionStage}&lt;{@link Map}&gt; in which the result of it's completion contains a map of all
     *          product keys -&gt; ids
     */
    @Nonnull
    CompletionStage<Map<String, String>> cacheKeysToIds();

    /**
     * Given a {@link Set} of product keys, this method fetches a set of all the products matching this given set of
     * keys in the CTP project defined in a potentially injected {@link SphereClient}.
     *
     * @param productKeys set of product keys to fetch matching products by.
     * @return {@link CompletionStage}&lt;{@link Map}&gt; in which the result of it's completion contains a {@link Set}
     *          of all matching products.
     */
    @Nonnull
    CompletionStage<Set<Product>> fetchMatchingProductsByKeys(@Nonnull final Set<String> productKeys);


    /**
     * Given a product key, this method fetches a product that matches this given key in the CTP project defined in a
     * potentially injected {@link SphereClient}. If there is no matching product an empty {@link Optional} will be
     * returned in the returned future.
     *
     * @param key the key of the product to fetch.
     * @return {@link CompletionStage}&lt;{@link Optional}&gt; in which the result of it's completion contains an
     *         {@link Optional} that contains the matching {@link Product} if exists, otherwise empty.
     */
    @Nonnull
    CompletionStage<Optional<Product>> fetchProduct(@Nullable final String key);

    /**
     * Given a {@link Set} of productsDrafts, this method creates Products corresponding to them in the CTP project
     * defined in a potentially injected {@link io.sphere.sdk.client.SphereClient}.
     *
     * @param productsDrafts set of productsDrafts to create on the CTP project.
     * @return {@link CompletionStage}&lt;{@link Map}&gt; in which the result of it's completion contains a {@link Set}
     *          of all created products.
     */
    @Nonnull
    CompletionStage<Set<Product>> createProducts(@Nonnull final Set<ProductDraft> productsDrafts);

    /**
     * Given a {@link ProductDraft}, this method creates a {@link Product} based on it in the CTP project defined in
     * a potentially injected {@link io.sphere.sdk.client.SphereClient}. The created product's id and key are also
     * cached. This method returns {@link CompletionStage}&lt;{@link Product}&gt; in which the result of it's
     * completion contains an instance of the {@link Product} which was created in the CTP project.
     *
     * @param productDraft the {@link ProductDraft} to create a {@link Product} based off of.
     * @return {@link CompletionStage}&lt;{@link Product}&gt; containing as a result of it's completion an instance of
     *          the {@link Product} which was created in the CTP project or a
     *          {@link io.sphere.sdk.models.SphereException}.
     */
    @Nonnull
    CompletionStage<Optional<Product>> createProduct(@Nonnull final ProductDraft productDraft);

    /**
     * Given a {@link Product} and a {@link List}&lt;{@link UpdateAction}&lt;{@link Product}&gt;&gt;, this method
     * issues an update request with these update actions on this {@link Product} in the CTP project defined in a
     * potentially injected {@link io.sphere.sdk.client.SphereClient}. This method returns
     * {@link CompletionStage}&lt;{@link Product}&gt; in which the result of it's completion contains an instance of
     * the {@link Product} which was updated in the CTP project.
     *
     * @param product       the {@link Product} to update.
     * @param updateActions the update actions to update the {@link Product} with.
     * @return {@link CompletionStage}&lt;{@link Product}&gt; containing as a result of it's completion an instance of
     *          the {@link Product} which was updated in the CTP project or a
     *          {@link io.sphere.sdk.models.SphereException}.
     */
    @Nonnull
    CompletionStage<Product> updateProduct(@Nonnull final Product product,
                                           @Nonnull final List<UpdateAction<Product>> updateActions);

    /**
     * Given a {@link Product}, this method issues an update request to publish this {@link Product} in the CTP project
     * defined in a potentially injected {@link io.sphere.sdk.client.SphereClient}. This method returns
     * {@link CompletionStage}&lt;{@link Product}&gt; in which the result of it's completion contains an instance of
     * the {@link Product} which was published in the CTP project.
     *
     * @param product the {@link Product} to publish.
     * @return {@link CompletionStage}&lt;{@link Product}&gt; containing as a result of it's completion an instance of
     *          the {@link Product} which was published in the CTP project or a
     *          {@link io.sphere.sdk.models.SphereException}.
     */
    @Nonnull
    CompletionStage<Product> publishProduct(@Nonnull final Product product);

    /**
     * Given a {@link Product}, this method issues an update request to revert the staged changes of this
     * {@link Product} in the CTP project defined in a potentially injected {@link io.sphere.sdk.client.SphereClient}.
     * This method returns {@link CompletionStage}&lt;{@link Product}&gt; in which the result of it's completion
     * contains an instance of the {@link Product} which had its staged changes reverted in the CTP project.
     *
     * @param product the {@link Product} to revert the staged changes for.
     * @return {@link CompletionStage}&lt;{@link Product}&gt; containing as a result of it's completion an instance of
     *          the {@link Product} which was published in the CTP project or a
     *          {@link io.sphere.sdk.models.SphereException}.
     */
    @Nonnull
    CompletionStage<Product> revertProduct(@Nonnull final Product product);
}
