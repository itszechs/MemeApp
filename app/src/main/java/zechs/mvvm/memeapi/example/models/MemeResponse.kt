package zechs.mvvm.memeapi.example.models


data class MemeResponse(
    val count: Int,
    val memes: MutableList<Meme>
)