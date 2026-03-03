import { Component, inject } from '@angular/core';
import { UserInfoComponent } from './user-info/user-info.component';
import { UserPreferencesComponent } from './user-preferences/user-preferences.component';
import { TranslationService } from '../../../services/translation.service';

@Component({
  selector: 'app-user-page',
  imports: [UserInfoComponent, UserPreferencesComponent],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.css'
})
export class UserPageComponent {

  translation = inject(TranslationService);

}
