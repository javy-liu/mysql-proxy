package com.mysql.proxy.protocol;

import java.nio.charset.Charset;

/**
 * http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-charsets.html
 * http://zennken.tistory.com/entry/Java-Charset-names-and-aliases
 * http://www.alessandrolacava.com/mapping-between-charset-name-and-java-name/
 * http://www.docjar.org/html/api/com/mysql/jdbc/CharsetMapping.java.html
 * http://cs.nmu.edu/~SeniorProjects/tflora/mysql-connector-java-5.0.4/src/com/mysql/jdbc/CharsetMapping.java
 * 
	# Charset, Description, Default collation, Maxlen
	big5, Big5 Traditional Chinese, big5_chinese_ci, 2
	dec8, DEC West European, dec8_swedish_ci, 1
	cp850, DOS West European, cp850_general_ci, 1
	hp8, HP West European, hp8_english_ci, 1
	koi8r, KOI8-R Relcom Russian, koi8r_general_ci, 1
	latin1, cp1252 West European, latin1_swedish_ci, 1
	latin2, ISO 8859-2 Central European, latin2_general_ci, 1
	swe7, 7bit Swedish, swe7_swedish_ci, 1
	ascii, US ASCII, ascii_general_ci, 1
	ujis, EUC-JP Japanese, ujis_japanese_ci, 3
	sjis, Shift-JIS Japanese, sjis_japanese_ci, 2
	hebrew, ISO 8859-8 Hebrew, hebrew_general_ci, 1
	tis620, TIS620 Thai, tis620_thai_ci, 1
	euckr, EUC-KR Korean, euckr_korean_ci, 2
	koi8u, KOI8-U Ukrainian, koi8u_general_ci, 1
	gb2312, GB2312 Simplified Chinese, gb2312_chinese_ci, 2
	greek, ISO 8859-7 Greek, greek_general_ci, 1
	cp1250, Windows Central European, cp1250_general_ci, 1
	gbk, GBK Simplified Chinese, gbk_chinese_ci, 2
	latin5, ISO 8859-9 Turkish, latin5_turkish_ci, 1
	armscii8, ARMSCII-8 Armenian, armscii8_general_ci, 1
	utf8, UTF-8 Unicode, utf8_general_ci, 3
	ucs2, UCS-2 Unicode, ucs2_general_ci, 2
	cp866, DOS Russian, cp866_general_ci, 1
	keybcs2, DOS Kamenicky Czech-Slovak, keybcs2_general_ci, 1
	macce, Mac Central European, macce_general_ci, 1
	macroman, Mac West European, macroman_general_ci, 1
	cp852, DOS Central European, cp852_general_ci, 1
	latin7, ISO 8859-13 Baltic, latin7_general_ci, 1
	utf8mb4, UTF-8 Unicode, utf8mb4_general_ci, 4
	cp1251, Windows Cyrillic, cp1251_general_ci, 1
	utf16, UTF-16 Unicode, utf16_general_ci, 4
	utf16le, UTF-16LE Unicode, utf16le_general_ci, 4
	cp1256, Windows Arabic, cp1256_general_ci, 1
	cp1257, Windows Baltic, cp1257_general_ci, 1
	utf32, UTF-32 Unicode, utf32_general_ci, 4
	binary, Binary pseudo charset, binary, 1
	geostd8, GEOSTD8 Georgian, geostd8_general_ci, 1
	cp932, SJIS for Windows Japanese, cp932_japanese_ci, 2
	eucjpms, UJIS for Windows Japanese, eucjpms_japanese_ci, 3
*/
public enum CharacterSet
{
	ASCII("US-ASCII"), ARMSCII8("ARMSCII8"), BIG5("Big5"), BINARY("BINARY"), CP1250("CP1250"),
	CP1251("CP1251"), CP1256("CP1256"), CP1257("CP1257"), CP850("CP850"), CP852("CP852"),
	CP866("CP866"), CP932("CP932"), DEC8("DEC8"), EUCJPMS("EUCJPMS"), EUCKR("EUC_KR"),
	GB2312("EUC_CN"), GBK("GBK"), GEOSTD8("GEOSTD8"), GREEK("ISO8859_7"), HEBREW("ISO8859_8"), 
	HP8("HP8"), KEYBCS2("KEYBCS2"), KOI8R("KOI8R"), KOI8U("KOI8U"), LATIN1("Cp1252"), 
	LATIN2("ISO8859_2"), LATIN5("LATIN5"), LATIN7("LATIN7"), MACCE("MacCentralEurope"), MACROMAN("MACROMAN"), 
	SJIS("SJIS"), SWE7("SWE7"), TIS620("TIS620"), UCS2("UnicodeBig"), UJIS("EUC_JP"), 
	UTF16("UTF-16"), UTF16LE("UTF-16LE"), UTF32("UTF-32"), UTF8("UTF-8"), UTF8MB4("UTF8MB4"); 
	
	private final Charset value;
	private final String encodingName;
	private CharacterSet(String javaEncodingName)
	{
		this.value = Charset.forName(javaEncodingName);
		this.encodingName = javaEncodingName;
	}
	
	public Charset toCharset() 
	{
	      return this.value;
	}
	
	public String getJavaEncodingName() 
	{
	      return this.encodingName;
	}
	
	public enum Collection
	{
		UTF8_GENERAL_CI;
		
		/**
		 * 컬렉션 - 아이디
		 */
		private int id;
		/**
		 * 이름
		 */
		private String name;
		/**
		 * 기본 컬렉션 여부
		 */
		private boolean isDefault;
		
	}
}
