package com.wooduan.xmlconfigmapper.processor.util

import javax.lang.model.element.Element

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 16:18
 */
class ProcessingException(val element: Element?, msg: String) : Exception(msg)
{
    constructor(element: Element?, msg: String, vararg params: Any) : this(element, msg.format(params))
}