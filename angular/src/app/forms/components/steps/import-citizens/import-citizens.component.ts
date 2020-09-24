import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {StepComponent} from "../../stepper/form-stepper/types";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-import-citizens',
  templateUrl: './import-citizens.component.html',
  styleUrls: ['./import-citizens.component.scss']
})
export class ImportCitizensComponent implements OnInit, StepComponent {

  constructor() { }

  ngOnInit(): void {
    this.files.append('eventId', 'ImportCitizens')
    this.form.addControl('fileName', new FormControl(''))
    this.form.addControl('fileSize', new FormControl(''))
  }

  @Input() files = new FormData()
  form: FormGroup = new FormGroup({})
  fileToUpload: File = null;

  valid(): boolean {
    return this.files.has('file');
  }

  validate: boolean;

  handleFileInput(files: FileList) {
    const file = files.item(0);
    if (file) {
      this.form.patchValue( {
        fileName: file.name,
        fileSize: file.size
      })
      this.files.append('file', file, file.name);
    }

  }
}
