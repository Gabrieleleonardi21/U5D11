package it.epicode.base.dto;

/**
 * Stesso endpoint, due forme: RobotPubblico per anonimi e utenti, RobotAdmin per l'admin.
 * Jackson serializza in base alla classe concreta, quindi la lista puo' contenere l'una o l'altra.
 */
public sealed interface RobotResponse permits RobotPubblico, RobotAdmin {
}
