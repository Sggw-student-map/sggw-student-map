export interface Review {
  id?: number;
  place_id: number;
  user_id?: number;
  rating: number;
  comment: string;
  author?: string;
  created_at?: string;
}