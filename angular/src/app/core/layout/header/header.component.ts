import { Component, OnInit, computed, signal, ChangeDetectorRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth-service.service';
import { TranslationService } from '../../../services/translation.service';
import { ThemeService } from '../../../services/theme.service';
import { UserPreferencesService } from '../../../services/user-page/user-preferences.service';
import { UserInfoService } from '../../../services/user-page/user-info.service';

@Component({
  selector: 'app-header',
  imports: [RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {

  isLoggedIn = signal<boolean>(false);

  loginText = computed(() => this.translation.translate('auth.login'));
  logoutText = computed(() => this.translation.translate('auth.logout'));
  userAreaText = computed(() => this.translation.translate('header.userArea'));
  languageText = computed(() => this.translation.translate('header.language'));
  currentLangDisplay = computed(() => this.translation.getCurrentLang());

  constructor(
    private userPreferencesService: UserPreferencesService,
    private userInfo: UserInfoService,
    public translation: TranslationService,
    public theme: ThemeService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.userInfo.isLoggedIn().subscribe(logged => {
      this.authService.sendUserSession(logged);
    });

    this.authService.getUserSession().subscribe(logged => {
      this.isLoggedIn.set(logged);
      this.cdr.detectChanges();
      if (logged) {
        this.userPreferencesService.getUserThemeLanguage().subscribe(prefs => {
          this.translation.setLanguage(prefs.language);
          this.theme.setTheme(prefs.theme);
        });
      }
    });
  }

  switchLanguage() {
    const newLang = this.translation.getCurrentLang() === 'ES' ? 'EN' : 'ES';
    this.translation.setLanguage(newLang);
  }

  switchTheme() {
    this.theme.toggleTheme();
  }
  
  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.sendUserSession(false);
    });
  }
}