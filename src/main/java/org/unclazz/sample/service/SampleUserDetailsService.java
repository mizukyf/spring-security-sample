package org.unclazz.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.unclazz.sample.SampleUserDetails;
import org.unclazz.sample.entity.User;
import org.unclazz.sample.mapper.SampleUserMapper;

/**
 * Spring Securityの認証メカニズムとアプリケーションのDBに格納された
 * ユーザのリレーションを橋渡しするためのサービス.
 * <p>{@link UserDetailsService}インターフェースを実装していることが重要な点である。</p>
 */
@Service
public class SampleUserDetailsService implements UserDetailsService {
	/**
	 * ユーザのリレーションを処理するためのORマッパー.
	 * <p>サンプルの元になったアプリケーションではMyBatisを用いているが、
	 * このサンプルアプリケーションではダミー実装に置き換えている。</p>
	 */
    @Autowired
    private SampleUserMapper userMapper;

    /**
     * 引数で指定されたユーザ名を使用してDBからユーザ情報を取得し、
     * Spring Securityの認証メカニズムで使用される{@link UserDetails}オブジェクトのかたちで返す.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	// 指定されたユーザ名をチェック
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        
        // ユーザ名をキーとして使ってユーザ情報を取得してみる
        final User user = userMapper.selectOneByName(username);
        
        // 取得できなかった場合のためのチェック
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User not found \"%s\"", username));
        }
        
        // レコード情報からUserDetailsオブジェクトを作成して返す
        return SampleUserDetails.of(user);
    }
}
