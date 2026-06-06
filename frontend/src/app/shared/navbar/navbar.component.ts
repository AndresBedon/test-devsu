import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
	selector: 'app-navbar',
    standalone: true,
    imports:  [RouterLink, RouterLinkActive],
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
	/*@Output() navigateTo = new EventEmitter<string>();

	menuOpen = false;

	toggleMenu(){
		this.menuOpen = !this.menuOpen;
	}

	navigate(route: string){
		this.navigateTo.emit(route);
	}*/
}
