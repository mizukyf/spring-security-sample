package org.unclazz.sample.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unclazz.sample.SampleSecurityConfiguration;
import org.unclazz.sample.SampleUserDetails;
import org.unclazz.sample.service.SampleUserService;

/**
 * サンプル・アプリケーションのコントローラ.
 * デモンストレーションのための最小限の機能しか持たせていない。
 * {@link SampleUserService}を使用したユーザ追加のロジックなど
 */
@Controller
public class SampleController {
//	サンプル・アプリケーションなので省いているが実際のアプリケーションでは
//	サービスを通じたユーザの登録・更新オペレーションも行えるにようすることになるだろう.
//	@Autowired
//	private SampleUserService userService;
	
	/**
	 * ログイン画面のレンダリングを制御するためのコントローラ.
	 * サンプル・アプリケーションなので戻り値によりビュー名を決めるしかしていない。
	 * @return ビュー名
	 */
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * トップ画面のレンダリングを制御するためのコントローラ.
     * <p>メソッド・シグネチャに{@link Principal}型の仮引数を指定すると、メソッド実行時に認証情報が設定される。
     * このコントローラが担当するURLとそこにアクセスできる認証状態・権限の組み合わせは
     * {@link SampleSecurityConfiguration}で行っている。</p>
     * @param principal プリンシパル
     * @param model モデル
     * @return ビュー名
     */
    @RequestMapping("/index")
    public String index(final Principal principal, final Model model) {
    	model.addAttribute("username", SampleUserDetails.of(principal).getUsername());
        return "index";
    }
    
    /**
     * アドミニストレータ画面のレンダリングを制御するためのコントローラ.
     * <p>メソッド・シグネチャに{@link Principal}型の仮引数を指定すると、メソッド実行時に認証情報が設定される。
     * このコントローラが担当するURLとそこにアクセスできる認証状態・権限の組み合わせは
     * {@link SampleSecurityConfiguration}で行っている。</p>
     * @param principal プリンシパル
     * @param model モデル
     * @return ビュー名
     */
    @RequestMapping("/admin")
    public String admin(final Principal principal, final Model model) {
    	model.addAttribute("username", SampleUserDetails.of(principal).getUsername());
        return "admin";
    }
}
