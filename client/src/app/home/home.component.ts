import { Component, ElementRef, ViewChild } from '@angular/core';
import { GoogleMap } from '@angular/google-maps';
import { ParkingSpaceService } from '../services/parking-space.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ReserveSpaceModalComponent } from '../reserve-space-modal/reserve-space-modal.component';
import { Observable, switchMap, timer } from 'rxjs';
import { ParkingSpace } from '../services/parking-space';
import { Subscription as RxJsSubscription } from 'rxjs/internal/Subscription';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  constructor(private parkingSpaceService: ParkingSpaceService, private dialog: MatDialog, private snackBar: MatSnackBar) {
  }

  readonly POOLLING_INTERVAL = 10 * 1000;

  readonly center = {
    lat: 42.6926736,
    lng: 23.309053
  }

  readonly options: google.maps.MapOptions = {
    fullscreenControl: false,
    streetViewControl: false,
    clickableIcons: false,
    mapTypeId: 'roadmap'
  };

  @ViewChild('mapSearchField') searchField!: ElementRef;

  @ViewChild(GoogleMap) map: GoogleMap;

  marker: any;

  currentAddress: string;

  private parkingSpacePolling$!: RxJsSubscription;

  private snackBarRef!: MatSnackBarRef<any>;

  private searchBox!: google.maps.places.SearchBox;

  ngAfterViewInit(): void {
    this.searchBox = new google.maps.places.SearchBox(this.searchField.nativeElement);
    this.map.controls[google.maps.ControlPosition.TOP_CENTER].push(this.searchField.nativeElement)

    this.searchBox.addListener('places_changed', () => {
      if (this.snackBarRef) {
        this.snackBarRef.dismiss();
      }
      const places = (this.searchBox.getPlaces() || []);
      if (places.length === 0) {
        return;
      }

      const bounds = new google.maps.LatLngBounds();
      let place = places[0];
      if (!place.geometry || !place.geometry.location) {
        return;
      }

      this.parkingSpaceService.findSpace(place.geometry.location.lat(), place.geometry.location.lng())
        .subscribe((parkingSpace) => {
          this.addMarker(parkingSpace.latitude, parkingSpace.longitude, parkingSpace.id);
          this.currentAddress = place.formatted_address || '';
        }, error => {
          if (error.status === 404) {
            this.snackBarRef = this.snackBar.open('Cannot find a free parking space near this location');
          }
        })

      if (place.geometry.viewport) {
        bounds.union(place.geometry.viewport);
      } else {
        bounds.extend(place.geometry.location);
      }
      this.map.fitBounds(bounds);
    });
  }

  addMarker(lat: number, lng: number, id: number) {
    this.marker = {
      position: {
        lat: lat,
        lng: lng,
      },
      label: {
        color: 'red',
        text: 'Free parking space',
      },
      title: 'Free parking space',
      options: {animation: google.maps.Animation.BOUNCE},
      parkingSpaceId: id,
      reserved: false,
    };
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;

    let matDialogRef = this.dialog.open(ReserveSpaceModalComponent, dialogConfig);

    matDialogRef.afterClosed().subscribe(startNavigation => {
      if (startNavigation) {
        if (this.marker.reserved) {
          let url = `https://maps.google.com/?daddr=${this.marker.position.lat},${this.marker.position.lng}`
          open(url);
        } else {
          this.parkingSpaceService.reserveSpace(this.marker.parkingSpaceId).subscribe(ok => {
            this.marker.title = 'Your parking space'
            this.marker.reserved = true;
            this.searchField.nativeElement.disabled = true;
            open(`https://maps.google.com/?daddr=${this.marker.position.lat},${this.marker.position.lng}`);
            this.parkingSpacePolling$ = this.startInfinitePollingOfParkingSpace(this.marker.parkingSpaceId)
              .subscribe(parkingSpace => {
                if (!parkingSpace.isFree) {
                  this.marker = null;
                  this.searchField.nativeElement.disabled = false;
                  this.parkingSpacePolling$.unsubscribe()
                  this.snackBarRef = this.snackBar.open('Your parking space has been taken. Find a new one!', 'Find');
                  this.snackBarRef.onAction().subscribe(() => {
                    this.searchField.nativeElement.value = this.currentAddress;
                    google.maps.event.trigger(this.searchField, 'focus', {});
                    google.maps.event.trigger(this.searchField, 'keydown', {keyCode: 13});  // enter
                    google.maps.event.trigger(this.searchBox, 'places_changed');
                  });
                }

                if (!parkingSpace.isReserved) {
                  this.marker = null;
                  this.searchField.nativeElement.disabled = false;
                  this.parkingSpacePolling$.unsubscribe()
                }
              })
          }, error => {
            console.log(error);
          })
        }

      }
    })
  }

  startInfinitePollingOfParkingSpace(id: number): Observable<ParkingSpace> {
    return timer(this.POOLLING_INTERVAL, this.POOLLING_INTERVAL)
      .pipe(switchMap(() => this.parkingSpaceService.getSpace(id)));
  }
}
