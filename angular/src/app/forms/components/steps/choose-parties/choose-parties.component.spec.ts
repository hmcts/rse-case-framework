import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChoosePartiesComponent } from './choose-parties.component';
import {RouterModule} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';

describe('ChoosePartiesComponent', () => {
  let component: ChoosePartiesComponent;
  let fixture: ComponentFixture<ChoosePartiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' }),
        HttpClientModule
      ],
      declarations: [ ChoosePartiesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChoosePartiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
