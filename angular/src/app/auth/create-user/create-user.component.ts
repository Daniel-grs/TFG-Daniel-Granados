import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { User } from '../../Dto/user-dtos';
import { NgIf } from '@angular/common';
import { UserService } from '../../services/user/user.service';
import { RouterLink } from "@angular/router";
import { AuthService } from '../../services/auth/auth-service.service';
import { TranslationService } from '../../services/translation.service';

@Component({
  selector: 'app-create-user',
  imports: [FormsModule, ReactiveFormsModule, NgIf, RouterLink],
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.css'
})
export class CreateUserComponent {

  translation = inject(TranslationService)

  form: FormGroup;

  user: User = {
    email: "",
    password: "",
    passwordConfirmation: "",
    name: "",
    surname: ""
  }

  constructor(private formBuilder: FormBuilder, private userService: UserService, private authService: AuthService) {
    this.form = formBuilder.group({
      email: ['',
        [
          Validators.required,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/),
        ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W)/)
      ]],
      passwordConfirmation: ['', [
        Validators.required
      ]],
      name: ['', [
        Validators.required
      ]],
      surname: ['', [
        Validators.required
      ]]
    }, {validator: this.passwordMatchValidator})
  }

  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }
  get passwordConfirmation() { return this.form.get('passwordConfirmation'); }
  get name() { return this.form.get('name'); }
  get surname() { return this.form.get('surname'); }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const passwordConfirmation = form.get('passwordConfirmation')?.value;
    if (password !== passwordConfirmation) {
      return { passwordMismatch: true };
    }
    return null;
  }
  
  hasError(controlName: string, errorName: string) {
        return (
            this.form.get(controlName)?.hasError(errorName) &&
            this.form.get(controlName)?.touched
        );
  }
  message: string = "";
  error: string = "";
  onSubmit() {
    if(this.form.valid){
      this.user.email = this.email?.value;
      this.user.password = this.password?.value;
      this.user.passwordConfirmation = this.passwordConfirmation?.value
      this.user.name = this.name?.value;
      this.user.surname = this.surname?.value;

      console.log(this.user)
      this.authService.saveUser(this.user).subscribe({
        next: () => {
          this.message = "Usuario creado con éxito."
          this.error = "";
      }, error: ()=>{
          this.error = "Ha occurido un error con los datos introducidos."
          this.message = ""
      }});
    } else{
      console.log("El formulario tiene errores.")
    }
   }
}
