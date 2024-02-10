package nonozi.freefamilytracking

class PostResponseMap(
    public var latitude: String,
    public var longitude: String,
    private var phonename: String,
) {
    override fun toString(): String {
        return "phonename=" + phonename + "\n" +
                "\t" + "lat=" + latitude + "\n" +
                "\t" + "lon=" + longitude
    }
}