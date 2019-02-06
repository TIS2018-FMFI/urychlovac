import { NgModule } from "@angular/core";
import { Routes, RouterModule } from '@angular/router';

import { LiveFeedComponent } from './live-feed/live-feed.component';
import { GraphComponent } from './graphs/graph.component';

const appRoutes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full'},
    { path: 'live-feed', component: LiveFeedComponent },
    { path: 'graphs', component: GraphComponent }
]

@NgModule({
    imports: [RouterModule.forRoot(appRoutes)],
    exports: [RouterModule]
})
export class AppRoutingModule {

}

//TODO: NETREBA!!! pravdepodobne, mozno kvoli Live feed