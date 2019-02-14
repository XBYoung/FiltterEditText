package com.young.l.mylibrary

import android.content.Context
import android.text.*
import android.util.AttributeSet
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern


class FilterEditext :EditText {
    private var isAllowEmoji = true
    private var isAllowSpace = true
    private var isAllowSpecial = true
    private var forbitFilterArry = arrayListOf<InputFilter>()

    var doAfter :((s:String)->Unit)? = null

    private var mContext: Context? = null
    constructor(context: Context?) : this(context,null){
       // initEditText(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val obtainStyledAttributes = context?.obtainStyledAttributes(attrs, R.styleable.FilterEditext)
        obtainStyledAttributes?.apply {
            isAllowEmoji = getBoolean(R.styleable.FilterEditext_allowEmoji,true)
            isAllowSpace = getBoolean(R.styleable.FilterEditext_allowSpace,true)
            isAllowSpecial = getBoolean(R.styleable.FilterEditext_allowSpecial,true)
            if (!isAllowEmoji){
                forbitFilterArry.add(emojiFilter)
            }
            if (!isAllowSpace){
                forbitFilterArry.add(spaceFilter)
            }
            if (!isAllowSpecial){
                forbitFilterArry.add(specialFilter)
            }
            recycle()
        }
        initEditText(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : this(context, attrs)

    private val specialFilter = InputFilter { source, start, end, dest, dstart, dend ->
        if (isSpecialText(source.toString())) {
            Toast.makeText(mContext, "不支持特殊字符", Toast.LENGTH_SHORT).show()
            ""
        }else{
            null
        }
    }
    private  val spaceFilter= InputFilter { source, start, end, dest, dstart, dend ->
        if (source.equals(" ")) {
            Toast.makeText(mContext, "不支持空格", Toast.LENGTH_SHORT).show()
        }

        if (source.equals(" ")){ "" }else{ null }
    }
    private  val emojiFilter = InputFilter { source, start, end, dest, dstart, dend -> if (containsEmoji(source.toString())){ "" }else{ null } }


    // 初始化edittext 控件
    private fun initEditText(context: Context?) {
        this.mContext = context
        val forbitFilters =arrayOfNulls<InputFilter>(forbitFilterArry.count())
        for ( i in 0 until  forbitFilterArry.count()){
           forbitFilters[i] = forbitFilterArry[i]
       }
        filters = forbitFilters
        addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                doAfter?.invoke(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    private fun containsEmoji(source: String): Boolean {

        val len = source.length
        for (i in 0 until len) {
            val codePoint = source[i]
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                Toast.makeText(mContext, "不支持输入Emoji表情符号", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private fun isEmojiCharacter(codePoint: Char): Boolean {
        return codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA ||
                codePoint.toInt() == 0xD || codePoint.toInt() >= 0x20 && codePoint.toInt() <= 0xD7FF ||
                codePoint.toInt() >= 0xE000 && codePoint.toInt() <= 0xFFFD || codePoint.toInt() >= 0x10000 && codePoint.toInt() <= 0x10FFFF
    }
    private fun isSpecialText(codePoint: String):Boolean{
           val speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
           val pattern = Pattern.compile(speChat);
           val matcher = pattern.matcher(codePoint)
           return matcher.find()
    }

}