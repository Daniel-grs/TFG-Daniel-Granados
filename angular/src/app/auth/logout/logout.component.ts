import { Component } from '@angular/core';
import { AuthService } from '../../services/auth/auth-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout',
  imports: [],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.css'
})
export class LogoutComponent {

  constructor(private authService: AuthService, private router: Router){}

  ngOnInit(){
    this.authService.logout();
    this.router.navigate(['/'])
    this.authService.sendUserSession(false);
  }
}
