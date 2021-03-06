package sogong.korea.prt_yazzikja

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import sogong.korea.prt_yazzikja.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_grid.view.*

class GridFragment :Fragment(){

    var mainView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_grid,container,false)
        mainView?.gridfragment_recyclerview?.adapter = GridFragmentRecyclerviewAdapter()
        mainView?.gridfragment_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
        return mainView
    }
    inner class GridFragmentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO>

        init{
            contentDTOs = ArrayList()
            FirebaseFirestore.getInstance().collection("images").orderBy("timestamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for(snapshot in querySnapshot!!.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var imageView = ImageView(parent.context)
            var width = resources.displayMetrics.widthPixels / 3
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageView)
        }

        private inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.imageView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
        }

    }
}