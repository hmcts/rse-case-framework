import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PartiesTabComponent } from './parties-tab.component';
import {HttpClientModule} from "@angular/common/http";

describe('Parties tab', () => {
  let component: PartiesTabComponent;
  let fixture: ComponentFixture<PartiesTabComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
        imports: [
          HttpClientModule,
        ],
      declarations: [ PartiesTabComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PartiesTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
