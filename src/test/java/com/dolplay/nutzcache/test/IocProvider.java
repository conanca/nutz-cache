package com.dolplay.nutzcache.test;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 实际项目中获取ioc的方式可灵活决定
 * @author conanca
 *
 */
public class IocProvider {
	private static Logger logger = LoggerFactory.getLogger(IocProvider.class);

	private static Ioc ioc;

	public static void init() {
		try {
			ioc = new NutIoc(new ComboIocLoader("*org.nutz.ioc.loader.json.JsonLoader", "nutzcacheioc.js", "dao.js",
					"*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "com.dolplay.nutzcache.test.service"));
		} catch (ClassNotFoundException e) {
			logger.error("Ioc create error", e);
		}
	}

	public static Ioc ioc() {
		if (ioc == null) {
			init();
		}
		return ioc;
	}
}
