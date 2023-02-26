package com.one.coreapp.ui.base.adapters

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.one.coreapp.utils.extentions.findBinding
import com.one.coreapp.utils.extentions.findGenericClassBySuperClass

abstract class ViewItemAdapter<out VI : ViewItemCloneable, out VB : ViewBinding>(
    private val onItemClick: (View, VI) -> Unit = { _, _ -> }
) {


    var adapter: BaseAsyncAdapter<*, *>? = null


    open fun getViewItemClass(): Class<ViewItemCloneable> {
        return findGenericClassBySuperClass(ViewItemCloneable::class.java)!!.java
    }


    open fun createViewItem(parent: ViewGroup, viewType: Int): VB {

        return findBinding(parent)
    }


    open fun onViewAttachedToWindow(holder: BaseBindingViewHolder<ViewBinding>, adapter: BaseAsyncAdapter<*, *>) {
        this.adapter = adapter
    }

    open fun onViewDetachedFromWindow(holder: BaseBindingViewHolder<ViewBinding>) {
        this.adapter = null
    }


    fun bindView(binding: @UnsafeVariance VB, viewType: Int, position: Int, item: @UnsafeVariance VI, payloads: MutableList<Any>) {

        bind(binding, viewType, position, item, payloads)
    }

    open fun bind(binding: @UnsafeVariance VB, viewType: Int, position: Int, item: @UnsafeVariance VI, payloads: MutableList<Any>) {
    }


    fun bindView(binding: @UnsafeVariance VB, viewType: Int, position: Int, item: @UnsafeVariance VI) {

        binding.root.setOnClickListener { view ->
            adapter?.currentList?.getOrNull(position)?.let { onItemClick.invoke(view, it as VI) }
        }

        bind(binding, viewType, position, item)
    }

    open fun bind(binding: @UnsafeVariance VB, viewType: Int, position: Int, item: @UnsafeVariance VI) {
    }
}

class MultiAdapter(

    vararg adapter: ViewItemAdapter<ViewItemCloneable, ViewBinding>,

    private val onLoadMore: (() -> Unit)? = null,

    private val onViewHolderAttachedToWindow: ((BaseBindingViewHolder<*>) -> Unit)? = null,
    private val onViewHolderDetachedFromWindow: ((BaseBindingViewHolder<*>) -> Unit)? = null,
) : BaseAsyncAdapter<ViewItemCloneable, ViewBinding>() {

    val list: List<ViewItemAdapter<ViewItemCloneable, ViewBinding>> by lazy {

        val adapters = arrayListOf<ViewItemAdapter<ViewItemCloneable, ViewBinding>>()

        adapters.add(LoadingViewAdapter() as ViewItemAdapter<ViewItemCloneable, ViewBinding>)
        adapters.add(LoadMoreViewAdapter(onLoadMore) as ViewItemAdapter<ViewItemCloneable, ViewBinding>)

        adapters.addAll(adapter)

        adapters
    }

    private val typeAndAdapter: Map<Int, ViewItemAdapter<ViewItemCloneable, ViewBinding>> by lazy {

        val map = HashMap<Int, ViewItemAdapter<ViewItemCloneable, ViewBinding>>()

        list.forEachIndexed { index, viewItemAdapter ->
            map[index] = viewItemAdapter
        }

        map
    }

    private val viewItemClassAndType: Map<Class<*>, Int> by lazy {

        val map = HashMap<Class<*>, Int>()

        list.forEachIndexed { index, viewItemAdapter ->
            map[viewItemAdapter.getViewItemClass()] = index
        }

        map
    }

    override fun getItemViewType(position: Int): Int {

        return viewItemClassAndType[getItem(position).javaClass] ?: super.getItemViewType(position)
    }

    override fun createView(parent: ViewGroup, viewType: Int): ViewBinding {

        return typeAndAdapter[viewType]!!.createViewItem(parent, viewType)
    }


    override fun onViewAttachedToWindow(holder: BaseBindingViewHolder<ViewBinding>) {
        super.onViewAttachedToWindow(holder)

        typeAndAdapter[holder.viewType]?.onViewAttachedToWindow(holder, this)

        onViewHolderAttachedToWindow?.invoke(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseBindingViewHolder<ViewBinding>) {
        super.onViewDetachedFromWindow(holder)

        typeAndAdapter[holder.viewType]?.onViewDetachedFromWindow(holder)

        onViewHolderDetachedFromWindow?.invoke(holder)
    }


    override fun bind(binding: ViewBinding, viewType: Int, position: Int, item: ViewItemCloneable, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        typeAndAdapter[viewType]?.bindView(binding, viewType, position, item, payloads)
    }

    override fun bind(binding: ViewBinding, viewType: Int, position: Int, item: ViewItemCloneable) {
        super.bind(binding, viewType, position, item)

        typeAndAdapter[viewType]?.bindView(binding, viewType, position, item)
    }
}


