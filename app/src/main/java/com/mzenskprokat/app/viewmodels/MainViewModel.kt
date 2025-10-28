package com.mzenskprokat.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mzenskprokat.app.models.*
import com.mzenskprokat.app.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel для главного экрана
class HomeViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _homeData = MutableStateFlow<Result<HomeData>>(Result.Loading)
    val homeData: StateFlow<Result<HomeData>> = _homeData.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            repository.getHomeData().collect { result ->
                _homeData.value = result
            }
        }
    }
}

// ViewModel для каталога продукции
class ProductCatalogViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<Result<List<Product>>>(Result.Loading)
    val products: StateFlow<Result<List<Product>>> = _products.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory.asStateFlow()

    init {
        loadAllProducts()
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { result ->
                _products.value = result
            }
        }
        _selectedCategory.value = null
    }

    fun filterByCategory(category: ProductCategory) {
        viewModelScope.launch {
            repository.getProductsByCategory(category).collect { result ->
                _products.value = result
            }
        }
        _selectedCategory.value = category
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            repository.getAllProducts().collect { result ->
                if (result is Result.Success) {
                    val filtered = result.data.filter { product ->
                        product.name.contains(query, ignoreCase = true) ||
                                product.alloys.any { it.contains(query, ignoreCase = true) }
                    }
                    _products.value = Result.Success(filtered)
                }
            }
        }
    }
}

// ViewModel для детального просмотра продукта
class ProductDetailViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _product = MutableStateFlow<Result<Product>>(Result.Loading)
    val product: StateFlow<Result<Product>> = _product.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            repository.getProductById(productId).collect { result ->
                _product.value = result
            }
        }
    }
}

// ViewModel для формы заказа
class OrderViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _orderState = MutableStateFlow<Result<Boolean>>(Result.Idle)
    val orderState: StateFlow<Result<Boolean>> = _orderState.asStateFlow()

    fun submitOrder(order: OrderRequest) {
        viewModelScope.launch {
            repository.submitOrder(order).collect { result ->
                _orderState.value = result
            }
        }
    }

    fun resetOrderState() {
        _orderState.value = Result.Idle
    }
}

// UI State для формы заказа
data class OrderFormState(
    val customerName: String = "",
    val phone: String = "",
    val email: String = "",
    val productName: String = "",
    val quantity: String = "",
    val comment: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
) {
    fun isValid(): Boolean {
        return customerName.isNotBlank() &&
                phone.isNotBlank() &&
                email.isNotBlank() &&
                productName.isNotBlank() &&
                quantity.isNotBlank()
    }
}