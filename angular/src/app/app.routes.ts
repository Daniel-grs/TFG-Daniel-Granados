import { Routes } from '@angular/router';
import { SearchBarComponent } from './features/pages/search-bar/search-bar.component';
import { CreateUserComponent } from './auth/create-user/create-user.component';
import { LoginComponent } from './auth/login/login.component';
import { UserPageComponent } from './features/pages/user-page/user-page.component';
import { LogoutComponent } from './auth/logout/logout.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
    {path: "", component: SearchBarComponent},
    {path: "register-user", component: CreateUserComponent},
    {path: "login", component: LoginComponent},
    {path: "logout", component: LogoutComponent},
    {path: "user-info", component: UserPageComponent, canActivate: [AuthGuard]},
];
