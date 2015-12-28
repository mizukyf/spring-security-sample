# Spring Security Sample

開発中のアプリケーションの実装方法調査のため種々しらべた結果を元に、Spring BootとSpring Securityを組み合わせて使うサンプルをつくってみました。アプリケーションの・・・とくに認証に関する要件は以下のとおりです：

* アプリケーションのユーザにはログインを義務付けたい
* アプリケーションのユーザにはオペレータとアドミニストレータの区別を付けたい
* ログインはユーザ名とパスワードによるシンプルなフォーム認証を利用したい

このリポジトリにコミットした内容を参照してもらうとして、ここでは重要な点をいくつかピックアップしておきます。

## `pom.xml`に依存性を追加する

Spring Securityのために`pom.xml`に依存性を追加します。当たり前な話ですがまずはここからです。`parent`のspring-boot-starter-parentのレベルでバージョン指定を行っているので、`dependency`のspring-boot-starter-securityのレベルでは指定していません：

```java:
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.1.12.RELEASE</version>
</parent>
<dependencies>
	...
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-security</artifactId>
	</dependency>
</dependencies>
```

## `WebSecurityConfigurerAdapter`を拡張する

アプリケーションの認証や権限付与に関する設定を行うため、 `WebSecurityConfigurerAdapter` を拡張したクラスを用意します。拡張するだけでなくアノテーションを付与している点も重要です。

ドキュメンテーション・コメントにもあるとおり、このオブジェクトを通じて特定のURLにアクセスする際に必要になる認証状態や保有権限の設定を行います。またRDBMSや何かしらの情報源からユーザ情報を取得するためのサービスの設定もここで行います。このとき登場する`UserDetailsService`についてはこの記事の最後のほうで紹介しています。

コード中には`PasswordEncorder`というインターフェースも登場していますが、これは文字通りパスワードを種々のアルゴリズムによりエンコードするためのものです。クライアントから送られてきたログイン情報（ユーザ名は`username`、パスワードは`password`という名前のリクエスト・パラメータで渡される）のうちパスワードの方はこのエンコーダにより処理されたうえで認証用のユーザ情報オブジェクト（後述）のプロパティに設定されます：

```java:
/**
 * Spring Securityのための設定情報を初期化するためのオブジェクト.
 * <p>{@link WebSecurityConfigurerAdapter}を継承（拡張）している点が重要。</p>
 * <p>このオブジェクトにより、認証せずにアクセスできるURL、認証せずにはアクセスできないURL、
 * 認証が必要なURLにアクセスしたときにリダイレクトされるURL、
 * 認証成功時にリダイレクトされるURL、ログアウトのトリガーとなるURLなど、認証−URL間の紐付けがなされる。</p>
 * <p>また同時に{@link GrantedAuthority}オブジェクトで表されるユーザの権限とURLの間の紐付けもなされる。</p>
 */
@Configuration
@EnableWebMvcSecurity
public class SampleSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /**
     * Spring Securityの認証メカニズムのためのVOを処理するサービス.
     */
	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * ユーザ認証とユーザ権限をユーザのアクセスURLと紐付けるための設定を行う.
	 * <p>引数として渡される{@link HttpSecurity}は一種のビルダーである。
	 * 開発者はこのオブジェクトを通じてユーザのアクセスするURLと認証状態・保有権限の紐付けや
	 * ログイン画面やログアウト画面の定義を行う。</p>
	 */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        
    	http.authorizeRequests()
    		// "/admin"というURLへのアクセスにはアドミニストレータ権限を必要とするよう設定
        	// ＊メソッド名から推測ができるようにAntっぽいワイルドカード表記が可能
        	.antMatchers("/admin").hasAuthority(SampleGrantedAuthority.NAME_ADMINISTRATOR)
        	// その他のURLについてはいずれにせよ認証をパスすることがアクセスの条件であると設定
            .anyRequest().authenticated()
            .and()
            // フォーム・ログインについての設定
            .formLogin()
            // 認証が必要なURLアクセスが行われた時は"/login"を表示するよう設定
            .loginPage("/login")
            // ログイン失敗時は"/login"を再表示するよう設定
            .failureUrl("/login")
            // ログイン成功後は"/index"に遷移するよう設定
            // ＊第2引数を省略するかfalseにすると、ログイン成功時
            // ログイン画面が表示される前そもそもユーザがアクセスを要求したURLへ遷移する
            .defaultSuccessUrl("/index", true)
            .permitAll()
            .and()
            // ログアウトについての設定
            .logout()
            // ログアウトのトリガーとなるURLを設定
        	// ＊オブジェクト名から推測ができるようにAntっぽいワイルドカード表記が可能
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            // ログアウト成功後のリダイレクト先を設定
            .logoutSuccessUrl("/login")
            .permitAll();
    }
    
    /**
     * このアプリケーションの認証メカニズムの設定を行う.
     * @param auth Spring Securityフレームワークから渡される認証管理ビルダー
     * @throws Exception {@link AuthenticationManagerBuilder#userDetailsService(UserDetailsService)}が例外をスローした場合
     */
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }
    ...
}
```

## `GrantedAuthority`を実装する

ユーザの権限をあらわすオブジェクトとして `GrantedAuthority`の実装を用意します。1つまえのサンプルコードでも登場した`SampleGrantedAuthority`です。これはインターフェース以外は何の変哲もないオブジェクトですので、サンプルコードは極々短めの抜粋とします：

```java:
/**
 * Spring Securityによる権限管理メカニズムで利用されるオブジェクト.
 * <p>{@link GrantedAuthority}インターフェースを実装している点が重要。</p>
 * <p>{@link SampleSecurityConfiguration}でユーザがアクセスするURLと権限との紐付けに利用するため、
 * 権限名を文字列型の{@code static final}フィールドとして公開している。
 * Spring Securityのメカニズムに即して言えばこの権現名こそ権限情報の本体である。</p>
 */
public class SampleGrantedAuthority implements GrantedAuthority {
...
	public static final String NAME_OPERATOR = "OPERATOR";
	public static final String NAME_ADMINISTRATOR = "ADMINISTRATOR";
...
	public static final SampleGrantedAuthority OPERATOR = new SampleGrantedAuthority(NAME_OPERATOR); 
	public static final SampleGrantedAuthority ADMINISTRATOR = new SampleGrantedAuthority(NAME_ADMINISTRATOR); 
...
}
```

## `o.s.s.core.userdetails.User`を拡張する

`org.springframework.security.core.userdetails.User`を拡張したクラスを用意します。パッケージ名付きの長ったらしい記載になりましたが、「User」ではアプリケーションの中の別のクラスと名前が衝突してしまうため、仕方なくこうしています。

ドキュメンテーション・コメントにも書きましたが、Spring Securityの認証メカニズムで利用されるVO（Value Object）です。リクエスト・パラメータやDBのリレーションの情報を格納するためのVOではなく、あくまでも認証メカニズムのために情報を格納するためのVOです。パスワードは前述のエンコーダによりエンコード済みである必要があります。

この記事にはあえて載せてはいませんがコントローラのメソッドのシグネチャに`Principal`型の仮引数がある場合、当該メソッドが呼び出される時にこの`SampleUserDetails`をラップしたオブジェクトが渡され、アプリケーション・ロジックから認証済みのユーザ情報にアクセスできるようになります。

なおORマッパーで利用するVOにあれこれとメソッドを追加したくなかったので、この`SampleUserDetails`のほうに相互変換用のメソッドを用意しました：

```java:
/**
 * Spring Securityの認証メカニズムで利用されるVO.
 * <p>{@link org.springframework.security.core.userdetails.User}を継承（拡張）している点が重要。
 * 継承元のオブジェクトではユーザIDという（DBのリレーション由来の）概念がないのでサブクラスでこれを補っている。
 * また継承元オブジェクトでは権限をコレクションとして保持しているが、サンプル・アプリケーションでは
 * オペレータかアドミニストレータかの区別が重要でそれ以外の権限を用意する想定がないため、
 * 2値を識別するためのフラグを追加している。</p>
 */
public class SampleUserDetails extends org.springframework.security.core.userdetails.User {
	public static SampleUserDetails of(final User user) { ... }
	public static SampleUserDetails of(final Principal principal) { ... }
	...	
	public int getId() { ... }
	public boolean isAdmin() { ... }
	public User toUser() { ... }
}
```

## `UserDetailsService`を実装する

最後にアプリケーション独自の永続化の仕組み（RDBMSやKVS、パスワードファイル等）とSpring Securityの認証メカニズムを橋渡しするため、`UserDetailsService`インターフェースを実装します。このオブジェクトは`WebSecurityConfigurerAdapter`のサンプルコードですでに登場しています。クライアントから渡されたパスワードの照合はSpring Security側で実施しますので、このオブジェクトの役割はユーザ名をもとにユーザ情報（前述の`....userdetails.User`）を生成して返すことだけです：

```java:
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
```

## その他のオブジェクト

サンプル・アプリケーションでは、上記のほか`User`オブジェクトや`SampleUserMapper`、`SampleController`など種々のコンポーネントが利用されていますが、それらはSpring Securityとはあまり関係がないのでここではとくに触れません。

反対に、これまでに個別に見てきたオブジェクトについてはいずれが欠けてもだめ（なはず）です。なかでも`WebSecurityConfigurerAdapter`はURLと認証・権限の制御を司るため重要性が高いオブジェクトです。

この記事にあえて載せてはいないコードも含めたアプリケーション全体の構成についてはリポジトリにコミットされた個々のリソースを参照してください。
