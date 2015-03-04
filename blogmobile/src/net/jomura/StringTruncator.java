package net.jomura;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 指定した文字エンコーディングでのバイト長で文字列をカットする。
 * 
 * <pre>
 * "あいうえお" --> EUC で 10 byte
 *             --> UTF-8 だと 15 byte
 * 
 * 半角カナなど入った場合は文字コードによってバイト数が 1 byte になったり、
 * 3 byte になったりします。DBの文字コードがAPサーバ側と異なっていて、
 *  DB に入るかどうか、APサーバ側で確認してから入れたいことがあります。
 * 
 * 本機能は「指定されたエンコーディングでのバイト数」を見て、文字列をカットし、
 * 返却する機能です。
 * 
 * StringTruncator.trunc("あいうえお", 7, "EUC-JP")   --> "あいう"
 * StringTruncator.trunc("あいうえお", 7, "UTF-8")    --> "あい"
 * StringTruncator.trunc("1234567890", 7, "UTF-8")   --> "1234567"
 * StringTruncator.trunc("1234567890", 7, "UTF-16")  --> "12"
 *  
 *  というような文字列が返却されます。
 *  </pre>
 */
public final class StringTruncator {
	private StringTruncator() {};
	/**
	 * 簡易版。<br>
	 * 
	 * エンコーディング処理が CharsetEncoder のデフォルト動作で問題無い場合は、
	 * こちらを使用してください。
	 * 
	 * @param str 処理対象となる文字列
	 * @param capacity カットしたいバイト数
	 * @param csn 文字エンコーディング("EUC-JP", "UTF-8" etc...)
	 * @return カットされた文字列
	 * @throws UnsupportedCharsetException 文字エンコーディングが不正
	 * @throws CharacterCodingException エンコーディング不正
	 */
	public static final String trunc(final String str, final int capacity, final String csn)
			throws UnsupportedCharsetException, CharacterCodingException {
		CharsetEncoder ce = Charset.forName(csn).newEncoder();
		if (capacity >= ce.maxBytesPerChar() * str.length())
			return str;
		CharBuffer cb = CharBuffer.wrap(new char[Math.min(str.length(), capacity)]);
		str.getChars(0, Math.min(str.length(), cb.length()), cb.array(), 0);
		return trunc(ce, cb, capacity).toString();
	}
	
	/**
	 * 詳細・高速版。<br>
	 * 
	 * エンコーディング処理を指定したい場合、CharsetEncoder を指定できる本機能を使用してください。
	 * エンコーディング中にエラーが出ても、その文字を「■」に置き換え、処理継続、といったような場合です。
	 * 
	 * 呼び出し側でバッファの確保を行いますので、繰り返し処理の中で本機能を呼び出すような場合は、
	 * 簡易版より高速になる場合があります。(1割から3割程度です。)
	 * 
	 * 内部でエンコードした文字列を置くためのワークエリアとして capacity バイト分の
	 * バイトバッファを作成します。
	 * 
	 * @param ce エンコーダ。
	 * @param in 処理対象となる文字列のバッファ
	 * @param capacity カットしたいバイト数
	 * @return in にて渡されたバッファを指定バイト数で flip() したもの
	 * @throws CharacterCodingException エンコーディング不正
	 */
	public static final CharBuffer trunc(final CharsetEncoder ce, final CharBuffer in,
			final int capacity) throws CharacterCodingException {
		if (capacity >= ce.maxBytesPerChar() * in.limit())
			return in;
		final ByteBuffer out = ByteBuffer.allocate(capacity);
		ce.reset();
		CoderResult cr = in.hasRemaining() ? ce.encode(in, out, true)
				: CoderResult.UNDERFLOW;
		if (cr.isUnderflow())
			cr = ce.flush(out);
		return (CharBuffer)in.flip();
	}
}