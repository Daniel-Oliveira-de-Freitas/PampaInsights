import { defineComponent, ref, onMounted } from 'vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import AkipAiService from '@/entities/akip-ai/akip-ai.service';
import { Message } from '@/shared/model/message.model';

export default defineComponent({
  components: { FontAwesomeIcon },
  setup() {
    const akipAiService = new AkipAiService();
    const chatInput = ref('');
    const messages = ref<Message[]>([]);
    const currentOutputMessageContent = ref('');
    const conversations = ref<any[]>([]);
    const selectedConversationId = ref('');

    onMounted(async () => {
      try {
        await retrieveConverations();
      } catch (error) {
        console.error('Error fetching conversations:', error);
      }
    });

    const retrieveConverations = async () => {
      akipAiService.getConversations().then(res => {
        conversations.value = res.data;
      });
    };

    const loadMessages = async (conversationId: string) => {
      selectedConversationId.value = conversationId;
      try {
        console.log(conversationId);
        const response = await akipAiService.getMessages(conversationId);
        messages.value = response.data;
      } catch (error) {
        console.error('Error loading messages:', error);
      }
    };

    const createNewConversation = async () => {
      akipAiService.createConversation().then(res => {
        const newConversation = res.data;
        selectedConversationId.value = newConversation.conversationId;
        messages.value = [new Message('agent', 'Hello, I am Akip AI GPT. How can I help you?')];
      });
    };

    const submitChat = async () => {
      if (chatInput.value.trim()) {
        if (!selectedConversationId.value) {
          await createNewConversation();
        }

        const inputMessage = new Message('user', chatInput.value);
        messages.value.push(inputMessage);
        chatInput.value = '';

        try {
          akipAiService.sendMessage(inputMessage, selectedConversationId.value!).then(res => {
            selectedConversationId.value = res.data.conversationId;
            console.log(res.data);
            messages.value = res.data.messages;
            retrieveConverations();
          });
        } catch (error) {
          console.error('Error submitting chat:', error);
          messages.value.push(new Message('agent', 'Sorry, there was an error. Please try again later.'));
        }
      }
    };

    return {
      chatInput,
      messages,
      currentOutputMessageContent,
      submitChat,
      conversations,
      selectedConversationId,
      loadMessages,
      createNewConversation,
    };
  },
});
