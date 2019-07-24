package io.github.keep2iron.app

class Recommend {

    var id: Int = 0
    var recommandName: String = ""
    var recommandImage: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recommend

        if (id != other.id) return false
        if (recommandName != other.recommandName) return false
        if (recommandImage != other.recommandImage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + recommandName.hashCode()
        result = 31 * result + recommandImage.hashCode()
        return result
    }


}