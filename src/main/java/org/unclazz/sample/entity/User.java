package org.unclazz.sample.entity;

import org.unclazz.sample.SampleUserDetails;

/**
 * アプリケーションのユーザ情報を保持するVO.
 * <p>{@link SampleUserDetails}がSpring Securityによる
 * 認証メカニズムのためのオブジェクトであるのに対して、
 * この{@code User}はリクエスト・パラメータやDBのリレーションを保持するためのオブジェクトである。</p>
 * <p>{@code User#admin}はこのユーザが管理者権限を持つかどうかを示すフラグ。
 * Spring Securityではロール（Role）や権限（Authority）という概念でアクセス制御を行うが、
 * このサンプルの元になったアプリケーションではオペレータとアドミニストレータを区別さえできれば事足りていたため、
 * DBでもこのフラグのみで権限情報を管理する想定でこのようなオブジェクト設計にしている。</p>
 */
public class User {
	private int id;
	private String name;
	private String password;
	private boolean admin;
	
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
