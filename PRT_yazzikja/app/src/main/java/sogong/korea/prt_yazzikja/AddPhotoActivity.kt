package sogong.korea.prt_yazzikja

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    val PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        addphoto_image.setOnClickListener { //Listener들은 전부 onCreate에 넣는듯? 사진을 클릭했을 때도 업로드가 가능하게끔
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //이곳에 모든 결과물이 다 모임.
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM){ //그래서 필터링이 필요. 요청 코드 대조를 통해 어떤 요청의 결과물인지 분류
            if(resultCode== Activity.RESULT_OK) { //사진을 선택했을 떄.
                photoUri = data?.data
                addphoto_image.setImageURI(data?.data)
            }
            else if(resultCode==Activity.RESULT_CANCELED){ //사진을 선택 안하면 그냥 메인으로 돌아감.
                finish()
            }
        }
    }

    fun contentUpload(){ //firebase에 파일 업로드
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "PNG_"+timestamp+"_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName) //child 는 하위경로.
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success),Toast.LENGTH_LONG).show()
        }
    }
}
