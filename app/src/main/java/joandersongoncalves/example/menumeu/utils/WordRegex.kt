package joandersongoncalves.example.menumeu.utils

import java.lang.StringBuilder

//class responsible for string to regex conversion
class WordRegex {
    companion object {
        fun getWordAsRegex(txt : String) : String {
            val sb = StringBuilder()
            for (c in txt) {
                if (c in 'a'..'z') {
                    sb.append("[").append(c-32).append(c).append("]")
                    continue
                }
                if (c in 'A'..'Z') {
                    sb.append("[").append(c).append(c+32).append("]")
                    continue
                }
                if (c == 'Ç') {
                    sb.append("[").append(c).append("ç").append("]")
                    continue
                }
                if (c == 'ç') {
                    sb.append("[").append("Ç").append(c).append("]")
                    continue
                }
                if (c == 'Ã') {
                    sb.append("[").append(c).append("ã").append("]")
                    continue
                }
                if (c == 'ã') {
                    sb.append("[").append("Ã").append(c).append("]")
                    continue
                }
                if (c == 'Á') {
                    sb.append("[").append(c).append("á").append("]")
                    continue
                }
                if (c == 'á') {
                    sb.append("[").append("Á").append(c).append("]")
                    continue
                }
                if (c == 'Â') {
                    sb.append("[").append(c).append("â").append("]")
                    continue
                }
                if (c == 'â') {
                    sb.append("[").append("Â").append(c).append("]")
                    continue
                }
                if (c == 'Ê') {
                    sb.append("[").append(c).append("ê").append("]")
                    continue
                }
                if (c == 'ê') {
                    sb.append("[").append("Ê").append(c).append("]")
                    continue
                }
                if (c == 'É') {
                    sb.append("[").append(c).append("é").append("]")
                    continue
                }
                if (c == 'é') {
                    sb.append("[").append("É").append(c).append("]")
                    continue
                }
                if (c == 'Õ') {
                    sb.append("[").append(c).append("õ").append("]")
                    continue
                }
                if (c == 'õ') {
                    sb.append("[").append("Õ").append(c).append("]")
                    continue
                }
                if (c == 'Ô') {
                    sb.append("[").append(c).append("ô").append("]")
                    continue
                }
                if (c == 'ô') {
                    sb.append("[").append("Ô").append(c).append("]")
                    continue
                }
                if (c == 'Ó') {
                    sb.append("[").append(c).append("ó").append("]")
                    continue
                }
                if (c == 'ó') {
                    sb.append("[").append("Ó").append(c).append("]")
                    continue
                }
                if (c == 'Í') {
                    sb.append("[").append(c).append("í").append("]")
                    continue
                }
                if (c == 'í') {
                    sb.append("[").append("Í").append(c).append("]")
                    continue
                }
                if (c == 'Ú') {
                    sb.append("[").append(c).append("ú").append("]")
                    continue
                }
                sb.append("[").append(c).append("]")
            }
            return sb.toString()
        }
    }

}