import { NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserLoginDTO } from '../../Dto/user-dtos';
import { UserService } from '../../services/user/user.service';
import { AuthService } from '../../services/auth/auth-service.service';
import { TranslationService } from '../../services/translation.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, ReactiveFormsModule, NgIf, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  translation = inject(TranslationService);

  form: FormGroup;
  userLogin: UserLoginDTO = {
    user: "",
    password: ""
  }
  constructor(private formBuilder: FormBuilder, private userService: UserService, private router: Router, private authService: AuthService) {
    this.form = formBuilder.group({
      user: ['',
        [
          Validators.required,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/)
        ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W)/)
      ]]
    })
  }
  get user() { return this.form.get('user'); }
  get password() { return this.form.get('password'); }
  error: string = "";
  message: string = "";
  onSubmit() {
      console.log(this.form.value)
      this.userLogin.user = this.user?.value
      this.userLogin.password = this.password?.value
      this.authService.loginUser(this.userLogin).subscribe({
        next: user => {
          console.log('Login component: login successful');
          this.authService.sendUserSession(true);
          this.router.navigate(['/']);
      }, error: () => {
          this.error = "Ha occurido un error con los datos introducidos.";
      }
    });

   }

   hasError(controlName: string, errorName: string) {
        return (
            this.form.get(controlName)?.hasError(errorName) &&
            this.form.get(controlName)?.touched
        );
  }
}