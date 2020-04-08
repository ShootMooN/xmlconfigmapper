package com.wooduan.xmlconfigmapper.processor.xml

import javax.lang.model.element.TypeElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 16:21
 */
interface XmlRootElement : XmlElement
{
    override val element : TypeElement
    val nameAsRoot : String
}