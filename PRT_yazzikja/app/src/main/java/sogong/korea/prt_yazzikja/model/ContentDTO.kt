package sogong.korea.prt_yazzikja.model

data class ContentDTO(var explain: String? = null,
                      var imageUrl : String? = null,
                      var uid : String? = null, //pk
                      var userId : String? = null, //email address.
                      var timestamp : Long? = null,
                      var favoriteCount : Int = 0, //좋아요 카운트
                      var favorites : Map<String,Boolean> = HashMap()){ //좋아요 중복 체크를 위함
    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp: Long? = null)
}