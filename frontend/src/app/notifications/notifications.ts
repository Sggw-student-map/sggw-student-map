import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './notifications.html',
})
export class Notifications {
  // Menu dopasowane do kolorów z widoku mapy
  menuItems = [
    { name: 'Feed', route: '/feed', colorClass: 'bg-blue-200' },
    { name: 'Wydarzenia', route: '/events', colorClass: 'bg-green-200' },
    { name: 'Opinie', route: '/opinions', colorClass: 'bg-pink-200' },
    { name: 'Znajomi', route: '/friends', colorClass: 'bg-orange-200' },
    { name: 'Powiadomienia', route: '/notifications', colorClass: 'bg-yellow-200' }
  ];

  // Testowe dane odwzorowujące obrazek
  notifications = [
    {
      id: 1,
      actorName: 'Jan Kowalski',
      actionType: 'COMMENT',
      actionText: 'skomentował Twoją opinię w miejscu',
      targetName: 'Wydział Zastosowań Informatyki i Matematyki',
      time: '12:11',
      isUnread: true,
      placeholderColor: 'bg-purple-400'
    },
    {
      id: 2,
      actorName: 'Anna Nowak',
      actionType: 'LIKE',
      actionText: 'polubił Twoją opinię w miejscu',
      targetName: 'Wydział Zastosowań Informatyki i Matematyki',
      time: 'wczoraj o 15:30',
      isUnread: true,
      placeholderColor: 'bg-green-400'
    },
    {
      id: 3,
      actorName: 'Marek Wiśniewski',
      actionType: 'NEW_OPINION',
      actionText: 'dodał nową opinię w miejscu, które obserwujesz:',
      targetName: 'Pomnik przyrody "Topole"',
      time: '2 dni temu',
      isUnread: false,
      placeholderColor: 'bg-yellow-400'
    }
  ];
}