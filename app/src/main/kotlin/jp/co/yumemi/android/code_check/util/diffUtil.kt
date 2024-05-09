// DiffUtil.kt
package jp.co.yumemi.android.code_check.util

import androidx.recyclerview.widget.DiffUtil
import jp.co.yumemi.android.code_check.model.RepositoryItem

/**
 * RepositoryItemのためのDiffUtil.ItemCallback実装。
 * RecyclerViewのアダプターでの効率的なアイテム変更の検出に使用される。
 */
val diffUtil = object : DiffUtil.ItemCallback<RepositoryItem>() {
    /**
     * 二つのアイテムが同じアイテムかどうかを判断する。
     * 通常、アイテムの一意識別子を比較するために使用される。
     *
     * @param oldItem 比較する古いリスト内のアイテム。
     * @param newItem 比較する新しいリスト内のアイテム。
     * @return 二つのアイテムが同じものである場合はtrue、そうでない場合はfalse。
     */
    override fun areItemsTheSame(oldItem: RepositoryItem, newItem: RepositoryItem): Boolean =
        oldItem.name == newItem.name

    /**
     * 二つのアイテムの内容が同じかどうかを判断する。
     * アイテムの内容が変更されていないかを検出するために使用される。
     *
     * @param oldItem 比較する古いリスト内のアイテム。
     * @param newItem 比較する新しいリスト内のアイテム。
     * @return 二つのアイテムの内容が同じである場合はtrue、そうでない場合はfalse。
     */
    override fun areContentsTheSame(oldItem: RepositoryItem, newItem: RepositoryItem): Boolean = oldItem == newItem
}