package sogong.korea.prt_yazzikja

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import sogong.korea.prt_yazzikja.model.ContentDTO

class DetailViewFragment : Fragment(){
    var firestore : FirebaseFirestore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        firestore = FirebaseFirestore.getInstance()

        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_detail,container,false)
        view.detailviewfragment_recycleview.adapter = DetailRecylerviewAdapter()
        view.detailviewfragment_recycleview.layoutManager = LinearLayoutManager(activity)
        return view
    }

    inner class DetailRecylerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val contentDTOs : ArrayList<ContentDTO>
        val contentUidList : ArrayList<String>

        init{
            contentDTOs = ArrayList()
            contentUidList = ArrayList()
            //현재 로그인 된 유저의 UID
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            //DB가 수정될 때 마다 수행된다 따라서 notify를 안에 넣어주어야 데이터가 바뀔때마다 호출된다.
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if(querySnapshot!=null) {
                    for (snapshot in querySnapshot!!.documents) {
                        //snapshot을 ContentDTO의 폼으로 매핑함. (object화)
                        var item = snapshot.toObject(ContentDTO::class.java)
                        println("입력되었다아아아아아")
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }

                    notifyDataSetChanged()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
            //var imageview = ImageView(parent.context)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView
            //유저아이디
            viewHolder.detailviewitem_profile_textview.text = contentDTOs!![position].userId
            //이미지 스레드 방식이라 다르게 씀.?
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_imageview_content)
            //설명텍스트
            viewHolder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            //좋아요 카운터 설정
            viewHolder.detailviewitem_favoritecounter_textview.text = "좋아요 " + contentDTOs!![position].favoriteCount + "개"
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            viewHolder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }

            //좋아요를 클릭
            if(contentDTOs!![position].favorites.containsKey(uid)) {

                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)

            //클릭하지 않았을 경우
            }else {
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

        }

        private fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction {
                transaction ->
                var uid = FirebaseAuth.getInstance().currentUser!!.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    //좋아요를 이미 누른 상태
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                }else{
                    //좋아요를 아직 누르지 않은 상태
                    contentDTO?.favorites[uid] = true
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1

                }
                transaction.set(tsDoc,contentDTO)
            }
        }

    }
}