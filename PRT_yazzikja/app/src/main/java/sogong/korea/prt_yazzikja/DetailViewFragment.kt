package sogong.korea.prt_yazzikja

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
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
            println("이게무슨일이야이게무슨일이야이게무슨일이야이게무슨일이야이게무슨일이야이게무슨일이야이게무슨일이야이게무슨일이야")
            if(firestore==null){
                println("firestore가 범인")
            }
            if(firestore?.collection("images")==null){
                println("collection이 범인")
            }
            if(firestore?.collection("images")?.orderBy("timestamp")==null){
                println("orderBy가 범인")
            }
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot!=null) {
                    println("여기다아아아아아아ㅏ아아아아아아아아아" + querySnapshot?.size() + "쿠쿠쿠쿠쿸쿠쿠쿠쿠쿠")
                            println(querySnapshot)
                    println("여기다아아아아아아ㅏ아아아아아아아아아" + querySnapshot?.size() + "쿠쿠쿠쿠쿸쿠쿠쿠쿠쿠")

                    for (snapshot in querySnapshot!!.documents) {
                        //snapshot을 ContentDTO의 폼으로 매핑함. (object화)
                        var item = snapshot.toObject(ContentDTO::class.java)
                        println("입력되었다아아아아아")
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }

                    notifyDataSetChanged()
                }
                else{
                    println("비었댄다비었댄다비었댄다비었댄다비었댄다비었댄다비었댄다")
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
            //var imageview = ImageView(parent.context)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            //(holder as CustomViewHolder).itemView as ImageView
        }

    }
}