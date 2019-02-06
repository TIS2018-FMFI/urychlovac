import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Status } from '../shared/status.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

@Component({
  selector: 'app-status-header',
  templateUrl: './status-header.component.html',
  styleUrls: ['./status-header.component.less']
})
export class StatusHeaderComponent implements OnInit {
  @ViewChild("lgModal") lgModal;
  modalRef: BsModalRef;

  selectedWarning: string = "";
  danger: boolean = false;
  primaryClass: string = 'label-primary';
  dangerClass: string = 'label-danger';
  successClass: string = 'label-success'
  white: string = 'white';
  off: string = '#17b338';
  on: string = 'red';

  statuses: Status[] = [
    new Status("Acc. doors", true, this.off, this.successClass), //modalne okno sa zavrie samo po 10sek
    new Status("SF6 leak", true, this.off, this.successClass),
    new Status("Cool. liquid state", false, this.on, this.dangerClass), //malo chladiacej kvapaliny
    new Status("Lab electricity", true, this.off, this.successClass),
    new Status("Acc. high voltage", true, this.off, this.successClass), //modalne okno, ked sa pristroj vypne - oranzove - automaticky sa vypne, 
                                                                                      //ked napatie padne - treba ho vypnut manualne 
    // new Status("aktuálnej hodnoty podtlaku a teploty v prehľadnej matici", false), TODO dafak?? :D
  ];

  constructor(private modalService: BsModalService) { }

  ngOnInit() {
  }

  // changeVoltageClass(){
  //   this.danger = !this.danger; //TODO: skusit spravit jednoduchsie pomocou ngStyle
  //   if(!this.danger) {
  //     this.voltage.nativeElement.classList.add(this.primaryClass);
  //     this.voltage.nativeElement.classList.remove(this.dangerClass);
  //   } else {
  //     this.voltage.nativeElement.classList.add(this.dangerClass);
  //     this.voltage.nativeElement.classList.remove(this.primaryClass);
  //   }
  // }

  onChange(index: number){ //TODO: skusit prerobit na Observable
    this.statuses[index].state = !this.statuses[index].state;
    if(this.statuses[index].color == this.on) { 
      this.statuses[index].color == this.off 
      return;
    } 
    
    this.statuses[index].color == this.on;
  }

  // openModal(template: TemplateRef<any>) {
  //   this.modalRef = this.modalService.show(template);
  // }

  switchColor(state: boolean){
    return state ? this.off : this.on;
  }

  setSelectedWarningOpenModal(name: string){
    this.selectedWarning = name;
    this.lgModal.show()
  }

}
