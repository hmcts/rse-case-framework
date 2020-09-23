import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportCitizensComponent } from './import-citizens.component';

describe('ImportCitizensComponent', () => {
  let component: ImportCitizensComponent;
  let fixture: ComponentFixture<ImportCitizensComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportCitizensComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportCitizensComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
