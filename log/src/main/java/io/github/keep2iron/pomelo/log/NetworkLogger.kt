package io.github.keep2iron.pomelo.log

import okhttp3.logging.HttpLoggingInterceptor

class NetworkLogger(private val logger: NetworkLoggingInterceptor.Logger) : HttpLoggingInterceptor.Logger {

    private val mMessage = StringBuilder()

    override fun log(message: String) {

        // 请求或者响应开始
        if (message.startsWith("--> POST")) {
            mMessage.setLength(0)
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        mMessage.append(
            if (message.startsWith("{") && message.endsWith("}") || message.startsWith("[") && message.endsWith("]")) {
                decodeUnicode(message).formatJson()
            } else {
                message
            } + "\n"
        )
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            logger.d(mMessage.toString())
        }
    }
}

internal fun addIndentBlank(sb: StringBuilder, indent: Int) {
    for (i in 0 until indent) {
        sb.append('\t')
    }
}

internal fun String.formatJson(): String {
    if (isNullOrEmpty()) return ""
    val sb = StringBuilder()
    var last: Char
    var current = '\u0000'
    var indent = 0
    for (i in 0 until length) {
        last = current
        current = this[i]
        //遇到{ [换行，且下一行缩进
        when (current) {
            '{', '[' -> {
                sb.append(current)
                sb.append('\n')
                indent++
                addIndentBlank(sb, indent)
            }
            //遇到} ]换行，当前行缩进
            '}', ']' -> {
                sb.append('\n')
                indent--
                addIndentBlank(sb, indent)
                sb.append(current)
            }
            //遇到,换行
            ',' -> {
                sb.append(current)
                if (last != '\\') {
                    sb.append('\n')
                    addIndentBlank(sb, indent)
                }
            }
            else -> sb.append(current)
        }
    }
    return sb.toString()
}

internal fun decodeUnicode(theString: String): String {
    var aChar: Char
    val len = theString.length
    val outBuffer = StringBuffer(len)
    var x = 0
    while (x < len) {
        aChar = theString[x++]
        if (aChar == '\\') {
            aChar = theString[x++]
            if (aChar == 'u') {
                var value = 0
                for (i in 0..3) {
                    aChar = theString[x++]
                    value = when (aChar) {
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> (value shl 4) + aChar.toInt() - '0'.toInt()
                        'a', 'b', 'c', 'd', 'e', 'f' -> (value shl 4) + 10 + aChar.toInt() - 'a'.toInt()
                        'A', 'B', 'C', 'D', 'E', 'F' -> (value shl 4) + 10 + aChar.toInt() - 'A'.toInt()
                        else -> throw IllegalArgumentException(
                            "Malformed   \\uxxxx   encoding."
                        )
                    }

                }
                outBuffer.append(value.toChar())
            } else {
                when (aChar) {
                    't' -> aChar = '\t'
                    'r' -> aChar = '\r'
                    'n' -> aChar = '\n'
                    'f' -> aChar = '\u000C'
                }
                outBuffer.append(aChar)
            }
        } else
            outBuffer.append(aChar)
    }
    return outBuffer.toString()
}