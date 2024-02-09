package nonozi.freefamilytracking

class PostResponseModel(
    private var latitude: String,
    private var longitude: String,
    private var phonename: String,
) {
    override fun toString(): String {
        return "Résponse du POST:\n" +
                "\t" + "phonename=" + phonename + "\n" +
                "\t" + "lat=" + latitude + "\n" +
                "\t" + "lon=" + longitude
    }
}