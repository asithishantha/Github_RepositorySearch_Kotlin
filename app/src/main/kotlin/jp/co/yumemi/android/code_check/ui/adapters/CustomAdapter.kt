// CustomAdapter.kt
package jp.co.yumemi.android.code_check.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.databinding.LayoutItemBinding
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.util.diffUtil
import com.bumptech.glide.Glide

/**
 * RecyclerViewのデータを表示するためのアダプタークラス。
 * RepositoryItemのリストを管理し、アイテムクリック時のイベントをlambda式で処理する。
 */
class CustomAdapter : ListAdapter<RepositoryItem, CustomAdapter.ItemViewHolder>(diffUtil) {

    /**
     * アイテムクリック時のコールバック関数。
     * RepositoryItemを引数に取り、クリックイベントを通知する。
     */
    var itemClickListener: ((RepositoryItem) -> Unit)? = null

    /**
     * ViewHolderを生成する。
     * LayoutInflaterを使用してLayoutItemBindingをinflateし、ItemViewHolderを初期化する。
     *
     * @param parent ViewHolderが存在するViewGroup
     * @param viewType 新しいViewのビュータイプ
     * @return 新しく作成されたItemViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, itemClickListener)
    }

    /**
     * 指定された位置にデータを表示するためにViewHolderをバインドする。
     * 現在のアイテムを取得し、ViewHolderのbindメソッドを呼び出す。
     *
     * @param holder バインドされるItemViewHolder
     * @param position リスト内のアイテム位置
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    /**
     * RecyclerViewの各アイテムを表すViewHolderクラス。
     * LayoutItemBindingを使用してアイテムのレイアウトを設定し、クリックイベントを処理する。
     *
     * @property binding アイテムビューをバインドするためのLayoutItemBinding
     * @property itemClickListener アイテムクリック時に呼び出されるコールバック関数
     */
    class ItemViewHolder(
        private val binding: LayoutItemBinding,
        private val itemClickListener: ((RepositoryItem) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * アイテムデータをViewHolderにバインドする。
         * レイアウトコンポーネントにアイテムのデータを設定し、クリックリスナーを設定する。
         *
         * @param item バインドされるRepositoryItem
         */
        fun bind(item: RepositoryItem) {
            binding.repositoryNameView.text = item.name
            binding.starCountTextView.text = item.stargazersCount.toString()

            Glide.with(binding.ownerIconImageView.context)
                .load(item.ownerIconUrl)
                .into(binding.ownerIconImageView)
            // アイテムビューがクリックされたときにitemClickListenerを呼び出す
            itemView.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }
    }
}