package org.unclazz.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unclazz.sample.SampleSecurityConfiguration;
import org.unclazz.sample.SampleUserDetails;
import org.unclazz.sample.entity.User;
import org.unclazz.sample.mapper.SampleUserMapper;

/**
 * リクエスト・パラーメタやリレーションとしてのユーザ情報を処理するためのサービス.
 */
@Service
public class SampleUserService {
	/**
	 * パスワードのエンコーダ.
	 * <p>{@link SampleSecurityConfiguration}
	 * で定義されたエンコーダが自動設定されるようにしている。</p>
	 */
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	/**
	 * ユーザのリレーションを処理するためのORマッパー.
	 * <p>サンプルの元になったアプリケーションではMyBatisを用いているが、
	 * このサンプルアプリケーションではダミー実装に置き換えている。</p>
	 */
    @Autowired
	private SampleUserMapper userMapper;
	
    /**
     * 新規ユーザを追加する.
     * @param name ユーザ名
     * @param rawPassord エンコード前のパスワード
     * @param admin 管理者権限を持つかどうか 
     * @param auth オペレーションを実行しているユーザの情報
     */
	public void registerUser(String name, CharSequence rawPassord, 
			boolean admin, SampleUserDetails auth) {
		// 引数で渡された情報をもとにリレーションのためのVOを初期化する
		final User user = new User();
		// IDはシーケンスから採番
		user.setId(userMapper.selectNextVal());
		// ユーザ名はそのまま使用
		user.setName(name);
		// パスワードはエンコードする
		user.setPassword(passwordEncoder.encode(rawPassord));
		// 管理者権限の有無をフラグで指定
		user.setAdmin(admin);
		// ORマッパーのメソッドを呼び出す
		userMapper.insert(user, auth);
	}
}
