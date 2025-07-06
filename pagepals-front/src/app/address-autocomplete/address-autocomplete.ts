import { Component, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, switchMap, tap } from 'rxjs/operators';
import { Subject, Observable, of } from 'rxjs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

export interface NominatimResult {
  display_name: string;
  lat: string;
  lon: string;
}

@Component({
  selector: 'app-address-autocomplete',
  imports: [FormsModule, ReactiveFormsModule, RouterModule, CommonModule],
  template: `
    <input type="text" class="form-control" placeholder="Adresse" (input)="onInput($event)" [value]="query" />
    <ul class="autocomplete-list" *ngIf="results.length > 0">
      <li *ngFor="let r of results" (click)="select(r)">{{ r.display_name }}</li>
    </ul>
  `,
  styles: [`
    .autocomplete-list {
      border: 1px solid #ccc;
      max-height: 200px;
      overflow-y: auto;
      margin: 0; padding: 0; list-style: none;
      position: absolute;
      background: white;
      width: 100%;
      z-index: 1000;
    }
    .autocomplete-list li {
      padding: 0.5rem;
      cursor: pointer;
    }
    .autocomplete-list li:hover {
      background-color: #ddd;
    }
  `]
})
export class AddressAutocomplete {
  @Output() addressSelected = new EventEmitter<NominatimResult>();
  
  query = '';
  results: NominatimResult[] = [];
  private inputSubject = new Subject<string>();

  constructor(private http: HttpClient) {
    this.inputSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => q ? this.search(q) : of([]))
    ).subscribe(results => this.results = results);
  }

  onInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.query = value;
    this.inputSubject.next(value);
  }

  search(query: string): Observable<NominatimResult[]> {
    const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&addressdetails=1&limit=5`;
    return this.http.get<NominatimResult[]>(url);
  }

  select(result: NominatimResult) {
    this.query = result.display_name;
    this.results = [];
    this.addressSelected.emit(result);
  }
}
