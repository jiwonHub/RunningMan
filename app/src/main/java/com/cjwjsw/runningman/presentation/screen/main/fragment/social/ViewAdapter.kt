import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.domain.model.FeedModel

class ViewAdapter(
    private val clickListener: OnItemClickListener
) : ListAdapter<FeedModel, ViewAdapter.Holder>(FeedDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(
            imageUrl: MutableList<String>,
            feedUid: MutableList<Char>,
            profileURL: String,
            title: String,
            content: String,
            likedCount: Int,
            isLiked: Boolean
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_recyclerview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val feedItem = getItem(position)
        val imageUrl = feedItem.imageUrls[0]

        // 이미지 로드
        Glide.with(holder.feedImageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.sun)
            .error(R.drawable.calories)
            .centerCrop()
            .into(holder.feedImageView)

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(
                feedItem.imageUrls.toMutableList(),
                feedItem.feedUID.toMutableList(),
                feedItem.profileURL,
                feedItem.title,
                feedItem.content,
                feedItem.likedCount,
                feedItem.isLiked
            )
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedImageView: ImageView = itemView.findViewById(R.id.feedImg)
    }

    // DiffUtil Callback
    class FeedDiffCallback : DiffUtil.ItemCallback<FeedModel>() {
        override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            return oldItem.feedUID == newItem.feedUID
        }

        override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            return oldItem == newItem
        }
    }
}
