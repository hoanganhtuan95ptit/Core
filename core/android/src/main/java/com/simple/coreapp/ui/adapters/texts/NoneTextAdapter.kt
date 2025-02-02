package com.simple.coreapp.ui.adapters.texts

import com.simple.adapter.ViewItemAdapter
import com.simple.coreapp.databinding.ItemTextBinding
import com.simple.coreapp.ui.view.Margin
import com.simple.coreapp.ui.view.Padding
import com.simple.coreapp.ui.view.Size
import com.simple.coreapp.ui.view.TextStyle
import com.simple.coreapp.ui.view.round.Background

open class NoneTextAdapter : ViewItemAdapter<NoneTextViewItem, ItemTextBinding>(), TextAdapter {

    override fun bind(binding: ItemTextBinding, viewType: Int, position: Int, item: NoneTextViewItem, payloads: MutableList<Any>) {

        binding(binding, viewType, position, item as TextViewItem, payloads)
    }

    override fun bind(binding: ItemTextBinding, viewType: Int, position: Int, item: NoneTextViewItem) {

        binding(binding, viewType, position, item as TextViewItem)
    }
}

data class NoneTextViewItem(
    override val id: String = "",
    override val data: Any? = null,

    override var text: CharSequence = "",

    override val size: Size? = null,
    override val image: Image? = null,
    override val margin: Margin? = null,
    override val padding: Padding? = null,
    override var textStyle: TextStyle? = null,
    override var background: Background? = null
) : TextViewItem()