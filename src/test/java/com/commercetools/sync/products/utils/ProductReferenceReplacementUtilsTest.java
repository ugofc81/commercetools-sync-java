package com.commercetools.sync.products.utils;

import com.commercetools.sync.commons.helpers.CategoryReferencePair;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.models.ResourceIdentifier;
import io.sphere.sdk.products.CategoryOrderHints;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductCatalogData;
import io.sphere.sdk.products.ProductData;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.states.State;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.commercetools.sync.products.ProductSyncMockUtils.getChannelMock;
import static com.commercetools.sync.products.ProductSyncMockUtils.getPriceMockWithChannelReference;
import static com.commercetools.sync.products.ProductSyncMockUtils.getProductVariantMockWithPrices;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductReferenceReplacementUtilsTest {

    @Test
    public void replaceProductsReferenceIdsWithKeys_WithSomeExpandedReferences_ShouldReplaceReferencesWhereExpanded() {
        final String resourceKey = "key";
        final ProductType productType = getProductTypeMock(resourceKey);
        final Reference<ProductType> productTypeReference =
            Reference.ofResourceTypeIdAndIdAndObj(ProductType.referenceTypeId(), productType.getId(), productType);
        final Reference<ProductType> nonExpandedProductTypeReference = ProductType.referenceOfId(productType.getId());

        final TaxCategory taxCategory = getTaxCategoryMock(resourceKey);
        final Reference<TaxCategory> taxCategoryReference =
            Reference.ofResourceTypeIdAndIdAndObj(TaxCategory.referenceTypeId(), taxCategory.getId(), taxCategory);
        final Reference<TaxCategory> nonExpandedTaxCategoryReference = TaxCategory.referenceOfId(taxCategory.getId());

        final State state = getStateMock(resourceKey);
        final Reference<State> stateReference =
            Reference.ofResourceTypeIdAndIdAndObj(State.referenceTypeId(), state.getId(), state);
        final Reference<State> nonExpandedStateReference = State.referenceOfId(state.getId());

        final Channel channel = getChannelMock(resourceKey);

        final Reference<Channel> channelReference = Reference
            .ofResourceTypeIdAndIdAndObj(Channel.referenceTypeId(), channel.getId(), channel);
        final Price price = getPriceMockWithChannelReference(channelReference);
        final ProductVariant productVariant = getProductVariantMockWithPrices(Collections.singletonList(price));

        final Product productWithNonExpandedProductType = getProductMock(Collections.singletonList(productVariant));

        when(productWithNonExpandedProductType.getProductType())
            .thenReturn(nonExpandedProductTypeReference);
        when(productWithNonExpandedProductType.getTaxCategory()).thenReturn(taxCategoryReference);
        when(productWithNonExpandedProductType.getState()).thenReturn(stateReference);

        final Product productWithNonExpandedTaxCategory =
            getProductMock(Collections.singletonList(productVariant));

        when(productWithNonExpandedTaxCategory.getProductType()).thenReturn(productTypeReference);
        when(productWithNonExpandedTaxCategory.getTaxCategory())
            .thenReturn(nonExpandedTaxCategoryReference);
        when(productWithNonExpandedTaxCategory.getState()).thenReturn(stateReference);

        final Product productWithNonExpandedSate =
            getProductMock(Collections.singletonList(productVariant));

        when(productWithNonExpandedSate.getProductType()).thenReturn(productTypeReference);
        when(productWithNonExpandedSate.getTaxCategory()).thenReturn(taxCategoryReference);
        when(productWithNonExpandedSate.getState()).thenReturn(nonExpandedStateReference);


        final List<Product> products = Arrays
            .asList(productWithNonExpandedProductType, productWithNonExpandedTaxCategory, productWithNonExpandedSate);


        final List<ProductDraft> productDraftsWithKeysOnReferences = ProductReferenceReplacementUtils
            .replaceProductsReferenceIdsWithKeys(products);

        assertThat(productDraftsWithKeysOnReferences).hasSize(3);

        assertThat(productDraftsWithKeysOnReferences.get(0).getProductType().getId()).isEqualTo(productType.getId());
        assertThat(productDraftsWithKeysOnReferences.get(0).getTaxCategory().getId()).isEqualTo(taxCategory.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(0).getState().getId()).isEqualTo(state.getKey());

        assertThat(productDraftsWithKeysOnReferences.get(1).getProductType().getId()).isEqualTo(productType.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(1).getTaxCategory().getId()).isEqualTo(taxCategory.getId());
        assertThat(productDraftsWithKeysOnReferences.get(1).getState().getId()).isEqualTo(state.getKey());

        assertThat(productDraftsWithKeysOnReferences.get(2).getProductType().getId()).isEqualTo(productType.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(2).getTaxCategory().getId()).isEqualTo(taxCategory.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(2).getState().getId()).isEqualTo(state.getId());

    }

    @Test
    public void replaceProductsReferenceIdsWithKeys_WithNullProducts_ShouldSkipNullProducts() {
        final String resourceKey = "key";
        final ProductType productType = getProductTypeMock(resourceKey);
        final Reference<ProductType> productTypeReference =
            Reference.ofResourceTypeIdAndIdAndObj(ProductType.referenceTypeId(), productType.getId(), productType);
        final Reference<ProductType> nonExpandedProductTypeReference = ProductType.referenceOfId(productType.getId());

        final TaxCategory taxCategory = getTaxCategoryMock(resourceKey);
        final Reference<TaxCategory> taxCategoryReference =
            Reference.ofResourceTypeIdAndIdAndObj(TaxCategory.referenceTypeId(), taxCategory.getId(), taxCategory);
        final Reference<TaxCategory> nonExpandedTaxCategoryReference = TaxCategory.referenceOfId(taxCategory.getId());

        final State state = getStateMock(resourceKey);
        final Reference<State> stateReference =
            Reference.ofResourceTypeIdAndIdAndObj(State.referenceTypeId(), state.getId(), state);

        final Channel channel = getChannelMock(resourceKey);

        final Reference<Channel> channelReference = Reference
            .ofResourceTypeIdAndIdAndObj(Channel.referenceTypeId(), channel.getId(), channel);
        final Price price = getPriceMockWithChannelReference(channelReference);
        final ProductVariant productVariant = getProductVariantMockWithPrices(Collections.singletonList(price));

        final Category category = getCategoryMock(resourceKey);
        final Reference<Category> categoryReference =
            Reference.ofResourceTypeIdAndIdAndObj(Category.referenceTypeId(), category.getId(), category);

        final Product productWithNonExpandedProductType =
            getProductMock(Collections.singleton(categoryReference), null,
                Collections.singletonList(productVariant));

        when(productWithNonExpandedProductType.getProductType())
            .thenReturn(nonExpandedProductTypeReference);
        when(productWithNonExpandedProductType.getTaxCategory()).thenReturn(taxCategoryReference);
        when(productWithNonExpandedProductType.getState()).thenReturn(stateReference);

        final Product productWithNonExpandedTaxCategory =
            getProductMock(Collections.singletonList(productVariant));

        when(productWithNonExpandedTaxCategory.getProductType()).thenReturn(productTypeReference);
        when(productWithNonExpandedTaxCategory.getTaxCategory())
            .thenReturn(nonExpandedTaxCategoryReference);
        when(productWithNonExpandedTaxCategory.getState()).thenReturn(stateReference);

        final List<Product> products = Arrays
            .asList(productWithNonExpandedProductType, productWithNonExpandedTaxCategory, null);


        final List<ProductDraft> productDraftsWithKeysOnReferences = ProductReferenceReplacementUtils
            .replaceProductsReferenceIdsWithKeys(products);

        assertThat(productDraftsWithKeysOnReferences).hasSize(2);

        assertThat(productDraftsWithKeysOnReferences.get(0).getProductType().getId()).isEqualTo(productType.getId());
        assertThat(productDraftsWithKeysOnReferences.get(0).getTaxCategory().getId()).isEqualTo(taxCategory.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(0).getState().getId()).isEqualTo(state.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(0).getMasterVariant().getPrices().get(0).getChannel().getId())
            .isEqualTo(channel.getKey());

        final Set<ResourceIdentifier<Category>> categoryResourceIdentifiers =
            productDraftsWithKeysOnReferences.get(0).getCategories();

        assertThat(categoryResourceIdentifiers).hasSize(1);
        assertThat(categoryResourceIdentifiers).containsExactly(Category.referenceOfId(category.getKey()));

        assertThat(productDraftsWithKeysOnReferences.get(1).getProductType().getId()).isEqualTo(productType.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(1).getTaxCategory().getId()).isEqualTo(taxCategory.getId());
        assertThat(productDraftsWithKeysOnReferences.get(1).getState().getId()).isEqualTo(state.getKey());
        assertThat(productDraftsWithKeysOnReferences.get(1).getMasterVariant().getPrices().get(0).getChannel().getId())
            .isEqualTo(channel.getKey());
    }

    @Test
    public void
        replaceProductTypeReferenceIdWithKey_WithNonExpandedReferences_ShouldReturnReferencesWithoutReplacedKeys() {
        final String productTypeId = UUID.randomUUID().toString();
        final Reference<ProductType> productTypeReference = ProductType.referenceOfId(productTypeId);
        final Product product = mock(Product.class);
        when(product.getProductType()).thenReturn(productTypeReference);

        final Reference<ProductType> productTypeReferenceWithKey = ProductReferenceReplacementUtils
            .replaceProductTypeReferenceIdWithKey(product);

        assertThat(productTypeReferenceWithKey).isNotNull();
        assertThat(productTypeReferenceWithKey.getId()).isEqualTo(productTypeId);
    }

    @Test
    public void replaceProductTypeReferenceIdWithKey_WithExpandedReferences_ShouldReturnReferencesWithReplacedKeys() {
        final String productTypeKey = "productTypeKey";

        final ProductType productType = getProductTypeMock(productTypeKey);

        final Reference<ProductType> productTypeReference = Reference
            .ofResourceTypeIdAndIdAndObj(ProductType.referenceTypeId(), productType.getId(), productType);

        final Product product = mock(Product.class);
        when(product.getProductType()).thenReturn(productTypeReference);

        final Reference<ProductType> productTypeReferenceWithKey = ProductReferenceReplacementUtils
            .replaceProductTypeReferenceIdWithKey(product);

        assertThat(productTypeReferenceWithKey).isNotNull();
        assertThat(productTypeReferenceWithKey.getId()).isEqualTo(productTypeKey);
    }

    @Test
    public void replaceProductTypeReferenceIdWithKey_WithNullReferences_ShouldReturnNull() {
        final Product product = mock(Product.class);
        when(product.getProductType()).thenReturn(null);

        final Reference<ProductType> productTypeReferenceWithKey = ProductReferenceReplacementUtils
            .replaceProductTypeReferenceIdWithKey(product);

        assertThat(productTypeReferenceWithKey).isNull();
    }

    @Test
    public void
        replaceTaxCategoryReferenceIdWithKey_WithNonExpandedReferences_ShouldReturnReferencesWithoutReplacedKeys() {
        final String taxCategoryId = UUID.randomUUID().toString();
        final Reference<TaxCategory> taxCategoryReference = TaxCategory.referenceOfId(taxCategoryId);
        final Product product = mock(Product.class);
        when(product.getTaxCategory()).thenReturn(taxCategoryReference);

        final Reference<TaxCategory> taxCategoryReferenceWithKey = ProductReferenceReplacementUtils
            .replaceTaxCategoryReferenceIdWithKey(product);

        assertThat(taxCategoryReferenceWithKey).isNotNull();
        assertThat(taxCategoryReferenceWithKey.getId()).isEqualTo(taxCategoryId);
    }

    @Test
    public void replaceTaxCategoryReferenceIdWithKey_WithExpandedReferences_ShouldReturnReferencesWithReplacedKeys() {
        final String taxCategoryKey = "taxCategoryKey";
        final TaxCategory taxCategory = getTaxCategoryMock(taxCategoryKey);
        final Reference<TaxCategory> taxCategoryReference = Reference
            .ofResourceTypeIdAndIdAndObj(TaxCategory.referenceTypeId(), taxCategory.getId(), taxCategory);

        final Product product = mock(Product.class);
        when(product.getTaxCategory()).thenReturn(taxCategoryReference);

        final Reference<TaxCategory> taxCategoryReferenceWithKey = ProductReferenceReplacementUtils
            .replaceTaxCategoryReferenceIdWithKey(product);

        assertThat(taxCategoryReferenceWithKey).isNotNull();
        assertThat(taxCategoryReferenceWithKey.getId()).isEqualTo(taxCategoryKey);
    }

    @Test
    public void replaceTaxCategoryReferenceIdWithKey_WithNullReference_ShouldReturnNull() {
        final Product product = mock(Product.class);
        when(product.getTaxCategory()).thenReturn(null);

        final Reference<TaxCategory> taxCategoryReferenceWithKey = ProductReferenceReplacementUtils
            .replaceTaxCategoryReferenceIdWithKey(product);

        assertThat(taxCategoryReferenceWithKey).isNull();
    }

    @Test
    public void replaceStateReferenceIdWithKey_WithNonExpandedReferences_ShouldReturnReferencesWithoutReplacedKeys() {
        final String stateId = UUID.randomUUID().toString();
        final Reference<State> stateReference = State.referenceOfId(stateId);
        final Product product = mock(Product.class);
        when(product.getState()).thenReturn(stateReference);

        final Reference<State> stateReferenceWithKey = ProductReferenceReplacementUtils
            .replaceProductStateReferenceIdWithKey(product);

        assertThat(stateReferenceWithKey).isNotNull();
        assertThat(stateReferenceWithKey.getId()).isEqualTo(stateId);
    }

    @Test
    public void replaceStateReferenceIdWithKey_WithExpandedReferences_ShouldReturnReferencesWithReplacedKeys() {
        final String stateKey = "stateKey";
        final State state = getStateMock(stateKey);
        final Reference<State> stateReference = Reference
            .ofResourceTypeIdAndIdAndObj(State.referenceTypeId(), state.getId(), state);

        final Product product = mock(Product.class);
        when(product.getState()).thenReturn(stateReference);

        final Reference<State> stateReferenceWithKey = ProductReferenceReplacementUtils
            .replaceProductStateReferenceIdWithKey(product);

        assertThat(stateReferenceWithKey).isNotNull();
        assertThat(stateReferenceWithKey.getId()).isEqualTo(stateKey);
    }

    @Test
    public void replaceStateReferenceIdWithKey_WithNullReferences_ShouldReturnNull() {
        final Product product = mock(Product.class);
        when(product.getState()).thenReturn(null);

        final Reference<State> stateReferenceWithKey = ProductReferenceReplacementUtils
            .replaceProductStateReferenceIdWithKey(product);

        assertThat(stateReferenceWithKey).isNull();
    }

    @Test
    public void buildProductQuery_Always_ShouldReturnQueryWithAllNeededReferencesExpanded() {
        final ProductQuery productQuery = ProductReferenceReplacementUtils.buildProductQuery();
        assertThat(productQuery.expansionPaths()).hasSize(10);
        assertThat(productQuery.expansionPaths())
            .containsExactly(ExpansionPath.of("productType"), ExpansionPath.of("taxCategory"),
                ExpansionPath.of("state"), ExpansionPath.of("masterData.staged.categories[*]"),
                ExpansionPath.of("masterData.staged.masterVariant.prices[*].channel"),
                ExpansionPath.of("masterData.staged.variants[*].prices[*].channel"),
                ExpansionPath.of("masterData.staged.masterVariant.attributes[*].value"),
                ExpansionPath.of("masterData.staged.variants[*].attributes[*].value"),
                ExpansionPath.of("masterData.staged.masterVariant.attributes[*].value[*]"),
                ExpansionPath.of("masterData.staged.variants[*].attributes[*].value[*]"));
    }

    @Test
    public void
        replaceCategoryReferencesIdsWithKeys_WithNonExpandedReferences_ShouldReturnReferencesWithoutReplacedKeys() {
        final String categoryId = UUID.randomUUID().toString();
        final Set<Reference<Category>> categoryReferences = Collections.singleton(Category.referenceOfId(categoryId));
        final CategoryOrderHints categoryOrderHints = getCategoryOrderHintsMock(categoryReferences);

        final Product product = getProductMock(categoryReferences, categoryOrderHints);

        final CategoryReferencePair categoryReferencePair =
            ProductReferenceReplacementUtils.replaceCategoryReferencesIdsWithKeys(product);

        assertThat(categoryReferencePair).isNotNull();

        final List<Reference<Category>> categoryReferencesWithKeys = categoryReferencePair.getCategoryReferences();
        final CategoryOrderHints categoryOrderHintsWithKeys = categoryReferencePair.getCategoryOrderHints();
        assertThat(categoryReferencesWithKeys).hasSize(1);
        assertThat(categoryReferencesWithKeys.get(0).getId()).isEqualTo(categoryId);
        assertThat(categoryOrderHintsWithKeys).isEqualTo(product.getMasterData().getStaged().getCategoryOrderHints());
    }

    @Test
    public void
        replaceCategoryReferencesIdsWithKeys_WithNonExpandedReferencesAndNoCategoryOrderHints_ShouldNotReplaceIds() {
        final String categoryId = UUID.randomUUID().toString();
        final Set<Reference<Category>> categoryReferences = Collections.singleton(Category.referenceOfId(categoryId));
        final Product product = getProductMock(categoryReferences, null);

        final CategoryReferencePair categoryReferencePair =
            ProductReferenceReplacementUtils.replaceCategoryReferencesIdsWithKeys(product);

        assertThat(categoryReferencePair).isNotNull();

        final List<Reference<Category>> categoryReferencesWithKeys = categoryReferencePair.getCategoryReferences();
        final CategoryOrderHints categoryOrderHintsWithKeys = categoryReferencePair.getCategoryOrderHints();
        assertThat(categoryReferencesWithKeys).hasSize(1);
        assertThat(categoryReferencesWithKeys.get(0).getId()).isEqualTo(categoryId);
        assertThat(categoryOrderHintsWithKeys).isEqualTo(product.getMasterData().getStaged().getCategoryOrderHints());
    }

    @Test
    public void replaceCategoryReferencesIdsWithKeys_WithExpandedReferences_ShouldReturnReferencesWithReplacedKeys() {
        final String categoryKey = "categoryKey";
        final Category category = getCategoryMock(categoryKey);
        final Reference<Category> categoryReference =
            Reference.ofResourceTypeIdAndIdAndObj(Category.referenceTypeId(), category.getId(), category);

        final Set<Reference<Category>> categoryReferences = Collections.singleton(categoryReference);
        final CategoryOrderHints categoryOrderHints = getCategoryOrderHintsMock(categoryReferences);

        final Product product = getProductMock(categoryReferences, categoryOrderHints);

        final CategoryReferencePair categoryReferencePair =
            ProductReferenceReplacementUtils.replaceCategoryReferencesIdsWithKeys(product);

        assertThat(categoryReferencePair).isNotNull();

        final List<Reference<Category>> categoryReferencesWithKeys = categoryReferencePair.getCategoryReferences();
        final CategoryOrderHints categoryOrderHintsWithKeys = categoryReferencePair.getCategoryOrderHints();
        assertThat(categoryReferencesWithKeys).hasSize(1);
        assertThat(categoryReferencesWithKeys.get(0).getId()).isEqualTo(categoryKey);
        assertThat(categoryOrderHintsWithKeys).isNotNull();
        assertThat(categoryOrderHintsWithKeys.getAsMap()).hasSize(1);
        assertThat(categoryOrderHintsWithKeys.get(categoryKey))
            .isEqualTo(product.getMasterData().getStaged().getCategoryOrderHints().getAsMap().get(category.getId()));
    }

    @Test
    public void replaceCategoryReferencesIdsWithKeys_WithExpandedReferencesAndNoCategoryOrderHints_ShouldReplaceIds() {
        final String categoryKey = "categoryKey";
        final Category category = getCategoryMock(categoryKey);
        final Reference<Category> categoryReference =
            Reference.ofResourceTypeIdAndIdAndObj(Category.referenceTypeId(), category.getId(), category);
        final Product product = getProductMock(Collections.singleton(categoryReference), null);

        final CategoryReferencePair categoryReferencePair =
            ProductReferenceReplacementUtils.replaceCategoryReferencesIdsWithKeys(product);

        assertThat(categoryReferencePair).isNotNull();

        final List<Reference<Category>> categoryReferencesWithKeys = categoryReferencePair.getCategoryReferences();
        final CategoryOrderHints categoryOrderHintsWithKeys = categoryReferencePair.getCategoryOrderHints();
        assertThat(categoryReferencesWithKeys).hasSize(1);
        assertThat(categoryReferencesWithKeys.get(0).getId()).isEqualTo(categoryKey);
        assertThat(categoryOrderHintsWithKeys).isNull();
    }

    @Test
    public void
        replaceCategoryReferencesIdsWithKeys_WithExpandedReferencesAndSomeCategoryOrderHintsSet_ShouldReplaceIds() {
        final String categoryKey1 = "categoryKey1";
        final String categoryKey2 = "categoryKey2";

        final Category category1 = getCategoryMock(categoryKey1);
        final Category category2 = getCategoryMock(categoryKey2);

        final Reference<Category> categoryReference1 =
            Reference.ofResourceTypeIdAndIdAndObj(Category.referenceTypeId(), category1.getId(), category1);
        final Reference<Category> categoryReference2 =
            Reference.ofResourceTypeIdAndIdAndObj(Category.referenceTypeId(), category2.getId(), category2);

        final Set<Reference<Category>> categoryReferences = new HashSet<>();
        categoryReferences.add(categoryReference1);
        categoryReferences.add(categoryReference2);

        final CategoryOrderHints categoryOrderHints =
            getCategoryOrderHintsMock(Collections.singleton(categoryReference1));

        final Product product = getProductMock(categoryReferences, categoryOrderHints);

        final CategoryReferencePair categoryReferencePair =
            ProductReferenceReplacementUtils.replaceCategoryReferencesIdsWithKeys(product);

        assertThat(categoryReferencePair).isNotNull();

        final List<Reference<Category>> categoryReferencesWithKeys = categoryReferencePair.getCategoryReferences();
        final CategoryOrderHints categoryOrderHintsWithKeys = categoryReferencePair.getCategoryOrderHints();
        assertThat(categoryReferencesWithKeys).hasSize(2);

        final List<String> referenceIds = categoryReferencesWithKeys.stream().map(Reference::getId)
                                                                    .collect(Collectors.toList());

        assertThat(referenceIds).contains(categoryKey1);
        assertThat(referenceIds).contains(categoryKey2);

        assertThat(categoryOrderHintsWithKeys).isNotNull();
        assertThat(categoryOrderHintsWithKeys.getAsMap()).hasSize(1);
        assertThat(categoryOrderHintsWithKeys.get(categoryKey1))
            .isEqualTo(product.getMasterData().getStaged().getCategoryOrderHints().getAsMap().get(category1.getId()));
    }

    @Test
    public void replaceCategoryReferencesIdsWithKeys_WithNoReferences_ShouldNotReplaceIds() {
        final Product product = getProductMock(Collections.emptySet(), null);

        final CategoryReferencePair categoryReferencePair =
            ProductReferenceReplacementUtils.replaceCategoryReferencesIdsWithKeys(product);

        assertThat(categoryReferencePair).isNotNull();

        final List<Reference<Category>> categoryReferencesWithKeys = categoryReferencePair.getCategoryReferences();
        final CategoryOrderHints categoryOrderHintsWithKeys = categoryReferencePair.getCategoryOrderHints();
        assertThat(categoryReferencesWithKeys).isEmpty();
        assertThat(categoryOrderHintsWithKeys).isNull();
    }



    @Nonnull
    private static Product getProductMock(@Nonnull final Set<Reference<Category>> references,
                                          @Nullable final CategoryOrderHints categoryOrderHints,
                                          @Nonnull final List<ProductVariant> productVariants) {
        final ProductData productData = mock(ProductData.class);
        mockProductDataCategories(references, categoryOrderHints, productData);
        mockProductDataVariants(productVariants, productData);
        return mockStagedProductData(productData);
    }

    @Nonnull
    private static Product getProductMock(@Nonnull final Set<Reference<Category>> references,
                                          @Nullable final CategoryOrderHints categoryOrderHints) {
        final ProductData productData = mock(ProductData.class);
        mockProductDataCategories(references, categoryOrderHints, productData);
        return mockStagedProductData(productData);
    }

    @Nonnull
    private static Product getProductMock(@Nonnull final List<ProductVariant> productVariants) {
        final ProductData productData = mock(ProductData.class);
        mockProductDataVariants(productVariants, productData);
        return mockStagedProductData(productData);
    }

    private static void mockProductDataCategories(@Nonnull final Set<Reference<Category>> references,
                                                  @Nullable final CategoryOrderHints categoryOrderHints,
                                                  @Nonnull final ProductData productData) {
        when(productData.getCategories()).thenReturn(references);
        when(productData.getCategoryOrderHints()).thenReturn(categoryOrderHints);
    }

    private static void mockProductDataVariants(@Nonnull final List<ProductVariant> productVariants,
                                                @Nonnull final ProductData productData) {
        if (!productVariants.isEmpty()) {
            final ProductVariant masterVariant = productVariants.get(0);
            final List<ProductVariant> variants = productVariants.subList(1, productVariants.size());

            when(productData.getMasterVariant()).thenReturn(masterVariant);
            when(productData.getVariants()).thenReturn(variants);
            when(productData.getAllVariants()).thenReturn(productVariants);
        }
    }

    @Nonnull
    private static CategoryOrderHints getCategoryOrderHintsMock(@Nonnull final Set<Reference<Category>> references) {
        final Map<String, String> categoryOrderHintMap = new HashMap<>();
        references.forEach(categoryReference -> categoryOrderHintMap.put(categoryReference.getId(), "0.1"));
        return CategoryOrderHints.of(categoryOrderHintMap);
    }

    @Nonnull
    private static Category getCategoryMock(@Nonnull final String key) {
        final Category category = mock(Category.class);
        when(category.getKey()).thenReturn(key);
        when(category.getId()).thenReturn(UUID.randomUUID().toString());
        return category;
    }

    @Nonnull
    private static ProductType getProductTypeMock(@Nonnull final String key) {
        final ProductType productType = mock(ProductType.class);
        when(productType.getKey()).thenReturn(key);
        when(productType.getId()).thenReturn(UUID.randomUUID().toString());
        return productType;
    }

    @Nonnull
    private static TaxCategory getTaxCategoryMock(@Nonnull final String key) {
        final TaxCategory taxCategory = mock(TaxCategory.class);
        when(taxCategory.getKey()).thenReturn(key);
        when(taxCategory.getId()).thenReturn(UUID.randomUUID().toString());
        return taxCategory;
    }

    @Nonnull
    private static State getStateMock(@Nonnull final String key) {
        final State state = mock(State.class);
        when(state.getKey()).thenReturn(key);
        when(state.getId()).thenReturn(UUID.randomUUID().toString());
        return state;
    }

    @Nonnull
    private static Product mockStagedProductData(@Nonnull final ProductData productData) {
        final ProductCatalogData productCatalogData = mock(ProductCatalogData.class);
        when(productCatalogData.getStaged()).thenReturn(productData);

        final Product product = mock(Product.class);
        when(product.getMasterData()).thenReturn(productCatalogData);
        return product;
    }
}
