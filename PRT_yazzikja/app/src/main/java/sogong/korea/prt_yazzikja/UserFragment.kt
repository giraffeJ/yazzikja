package sogong.korea.prt_yazzikja

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import sogong.korea.prt_yazzikja.model.ContentDTO

class UserFragment : Fragment(){
    var fragmentView : View? = null
    var PICK_PROFILE_FOR_ALBUM = 10
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        fragmentView = inflater.inflate(R.layout.fragment_user,container, false)
        fragmentView?.account_iv_profile?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FOR_ALBUM)
        }
        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity!!,3)
        getProfileImages()
        return fragmentView
    }

    fun getProfileImages(){
        firestore?.collection("profileimages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot?.data != null){
                var url = documentSnapshot?.data!!["image"]
                Glide.with(activity).load(url).apply(RequestOptions().circleCrop()).into(fragmentView!!.account_iv_profile)
            }
        }
    }
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO>
        init {
            contentDTOs = ArrayList()
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (querySnapshot != null) {
                    for(snapshot in querySnapshot.documents){
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    account_tv_post_count.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

    }

}