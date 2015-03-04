package net.jomura;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;

/**
 * �w�肵�������G���R�[�f�B���O�ł̃o�C�g���ŕ�������J�b�g����B
 * 
 * <pre>
 * "����������" --> EUC �� 10 byte
 *             --> UTF-8 ���� 15 byte
 * 
 * ���p�J�i�ȂǓ������ꍇ�͕����R�[�h�ɂ���ăo�C�g���� 1 byte �ɂȂ�����A
 * 3 byte �ɂȂ����肵�܂��BDB�̕����R�[�h��AP�T�[�o���ƈقȂ��Ă��āA
 *  DB �ɓ��邩�ǂ����AAP�T�[�o���Ŋm�F���Ă�����ꂽ�����Ƃ�����܂��B
 * 
 * �{�@�\�́u�w�肳�ꂽ�G���R�[�f�B���O�ł̃o�C�g���v�����āA��������J�b�g���A
 * �ԋp����@�\�ł��B
 * 
 * StringTruncator.trunc("����������", 7, "EUC-JP")   --> "������"
 * StringTruncator.trunc("����������", 7, "UTF-8")    --> "����"
 * StringTruncator.trunc("1234567890", 7, "UTF-8")   --> "1234567"
 * StringTruncator.trunc("1234567890", 7, "UTF-16")  --> "12"
 *  
 *  �Ƃ����悤�ȕ����񂪕ԋp����܂��B
 *  </pre>
 */
public final class StringTruncator {
	private StringTruncator() {};
	/**
	 * �ȈՔŁB<br>
	 * 
	 * �G���R�[�f�B���O������ CharsetEncoder �̃f�t�H���g����Ŗ�薳���ꍇ�́A
	 * ��������g�p���Ă��������B
	 * 
	 * @param str �����ΏۂƂȂ镶����
	 * @param capacity �J�b�g�������o�C�g��
	 * @param csn �����G���R�[�f�B���O("EUC-JP", "UTF-8" etc...)
	 * @return �J�b�g���ꂽ������
	 * @throws UnsupportedCharsetException �����G���R�[�f�B���O���s��
	 * @throws CharacterCodingException �G���R�[�f�B���O�s��
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
	 * �ڍׁE�����ŁB<br>
	 * 
	 * �G���R�[�f�B���O�������w�肵�����ꍇ�ACharsetEncoder ���w��ł���{�@�\���g�p���Ă��������B
	 * �G���R�[�f�B���O���ɃG���[���o�Ă��A���̕������u���v�ɒu�������A�����p���A�Ƃ������悤�ȏꍇ�ł��B
	 * 
	 * �Ăяo�����Ńo�b�t�@�̊m�ۂ��s���܂��̂ŁA�J��Ԃ������̒��Ŗ{�@�\���Ăяo���悤�ȏꍇ�́A
	 * �ȈՔł�荂���ɂȂ�ꍇ������܂��B(1������3�����x�ł��B)
	 * 
	 * �����ŃG���R�[�h�����������u�����߂̃��[�N�G���A�Ƃ��� capacity �o�C�g����
	 * �o�C�g�o�b�t�@���쐬���܂��B
	 * 
	 * @param ce �G���R�[�_�B
	 * @param in �����ΏۂƂȂ镶����̃o�b�t�@
	 * @param capacity �J�b�g�������o�C�g��
	 * @return in �ɂēn���ꂽ�o�b�t�@���w��o�C�g���� flip() ��������
	 * @throws CharacterCodingException �G���R�[�f�B���O�s��
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