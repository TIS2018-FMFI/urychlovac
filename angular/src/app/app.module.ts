import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { StatusHeaderComponent } from './status-header/status-header.component';
import { LiveFeedComponent } from './live-feed/live-feed.component';
import { GraphComponent } from './graphs/graph.component';
import { AppRoutingModule } from './app-routing.module'
import { Ng2HighchartsModule } from 'ng2-highcharts';
import { FormsModule } from '@angular/forms';
import { ModalModule } from 'ngx-bootstrap/modal';

@NgModule({
  declarations: [
    AppComponent,
    StatusHeaderComponent,
    LiveFeedComponent,
    GraphComponent,
  ],
  imports: [
    BrowserModule,
    Ng2HighchartsModule,
    AppRoutingModule,
    FormsModule,
    ModalModule.forRoot(),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
