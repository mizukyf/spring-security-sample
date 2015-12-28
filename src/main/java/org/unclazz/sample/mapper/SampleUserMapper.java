package org.unclazz.sample.mapper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;
import org.unclazz.sample.SampleUserDetails;
import org.unclazz.sample.entity.User;

/**
 * アプリケーションのユーザ情報をDBから取得するORマッパー.
 * <p>サンプルなので実際にはDBにはアクセスせずあらかじめ決められた名前に対して
 * 決められた{@code User}オブジェクトを返すだけにしてある。</p>
 * <p>ユーザのパスワードは{@link StandardPasswordEncoder}によりエンコードされた状態で
 * DBに格納されている想定。DBから取得したVOのプロパティにもこのエンコード済みパスワードが設定されている。</p>
 */
@Component
public class SampleUserMapper {
	/**
	 * ダミーのユーザ情報を格納するマップ.
	 */
	private static final Map<String, User> databaseDummy = new HashMap<String, User>();
	
	static {
		// ダミーユーザ1　ユーザ名はfoo、パスワードはbar
		// ＊パスワードはStandardPasswordEncorderによりエンコードされている
		final User foo = new User();
		foo.setId(1);
		foo.setName("foo");
		foo.setPassword("1b6337e4f7dde5e763f0867666a2f421a9f855938ff88ddf8c81a10e16493e8b235e74e5d43d7ae1");
		foo.setAdmin(true);
		
		// ダミーユーザ2　ユーザ名はfoo2、パスワードはbar2
		// ＊こちらもパスワードはエンコードされている
		final User foo2 = new User();
		foo2.setId(2);
		foo2.setName("foo2");
		foo2.setPassword("54dac60766b07a02f01eafca7180e3053e69b607535e31f7786eeedccd74fc9feac6235a766f712d");
		foo2.setAdmin(false);
		
		databaseDummy.put("foo", foo);
		databaseDummy.put("foo2", foo2);
	}
	
	/**
	 * ユーザ名をキーにしてユーザ情報をDBで検索し結果をVOのかたちで返す.
	 * @param name ユーザ名
	 * @return ユーザ情報VO
	 */
	public User selectOneByName(String name) {
		return databaseDummy.get(name);
	}
	
	/**
	 * 新しいユーザのためのIDをシーケンスを使って採番する.
	 * @return ID
	 */
	public int selectNextVal() {
		return databaseDummy.size() + 1;
	}
	
	/**
	 * 新しいユーザをDBに登録する.
	 * @param user リレーションのためのVO
	 * @param auth 認証メカニズムのためのVO（作成者IDや更新者IDの記録に利用）
	 */
	public void insert(User user, SampleUserDetails auth) {
		final String username = user.getName();
		if (databaseDummy.containsKey(username)) {
			throw new RuntimeException(String.format("Duplicated username \"%s\".", username));
		}
		databaseDummy.put(username, user);
	}
	
	/**
	 * 既存のユーザを更新する.
	 * @param user リレーションのためのVO
	 * @param auth 認証メカニズムのためのVO（作成者IDや更新者IDの記録に利用）
	 */
	public void update(User user, SampleUserDetails auth) {
		final String username = user.getName();
		if (!databaseDummy.containsKey(username)) {
			throw new RuntimeException(String.format("Unknown username \"%s\".", username));
		}
		databaseDummy.put(username, user);
	}
}
