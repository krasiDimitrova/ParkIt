export class ParkingSpace {
  id: number;
  latitude: number;
  longitude: number;
  isFree: boolean;
  isReserved: boolean;

  constructor(id: number, latitude: number, longitude: number,
              isFree: boolean, isReserved: boolean) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.isFree = isFree;
    this.isReserved = isReserved;
  }
}
