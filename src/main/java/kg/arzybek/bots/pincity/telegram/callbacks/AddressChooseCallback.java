package kg.arzybek.bots.pincity.telegram.callbacks;

import kg.arzybek.bots.pincity.data.PlacesRepository;
import kg.arzybek.bots.pincity.dto.PinDto;
import kg.arzybek.bots.pincity.dto.PinState;
import kg.arzybek.bots.pincity.utils.Consts;
import kg.arzybek.bots.pincity.utils.JsonHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AddressChooseCallback implements CallbackHandler {

    private final PlacesRepository placesRepository;

    public SendMessage apply(Callback callback, Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage answer;
        Integer addressId = Integer.valueOf(callback.getData());
        PinDto pin = placesRepository.findPin(addressId);
        if (pin.getPin() == null) {
            answer = new SendMessage(String.valueOf(chatId), Consts.CHOSE_ADDRESS_NO_PIN);
            addPinActionsKeyboard(answer, addressId);
        } else if (pin.getPinState() == PinState.OUTDATED) {
            answer = new SendMessage(String.valueOf(chatId), String.format(Consts.CHOSE_ADDRESS_OUTDATED_PIN, pin.getPin()));
            addActionsKeyboard(answer, addressId, chatId);
        } else {
            answer = new SendMessage(String.valueOf(chatId), String.format(Consts.CHOSE_ADDRESS_PIN, pin.getPin()));
            addActionsKeyboard(answer, addressId, chatId);
        }
        return answer;
    }

    public static void addPinActionsKeyboard(SendMessage answer, Integer addressId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(Consts.YES);
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.PIN_ADD, addressId));
        inlineKeyboardButton.setCallbackData(jsonCallback);
        keyboardButtonsRow.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(Consts.NO);
        String jsonCallback1 = JsonHandler.toJson(List.of(CallbackType.PIN_DONT_ADD, addressId));
        inlineKeyboardButton1.setCallbackData(jsonCallback1);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        answer.setReplyMarkup(inlineKeyboardMarkup);
    }

    private void addActionsKeyboard(SendMessage answer, Integer addressId, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(Consts.YES);
        String jsonCallback = JsonHandler.toJson(List.of(CallbackType.PIN_OK, addressId));
        inlineKeyboardButton.setCallbackData(jsonCallback);
        keyboardButtonsRow.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(Consts.NO);
        String jsonCallback1 = JsonHandler.toJson(List.of(CallbackType.PIN_WRONG, addressId));
        inlineKeyboardButton1.setCallbackData(jsonCallback1);
        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        answer.setReplyMarkup(inlineKeyboardMarkup);
    }

}
